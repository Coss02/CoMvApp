package com.example.comovapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.comovapp.cell.Cell;
import com.example.comovapp.cell.CellLTE;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.comovapp.databinding.ActivityMapViewBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class mapView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TelephonyData telephonyData;
    private ActivityMapViewBinding binding;
    private int buttonPressCount = 0;
    private int STAGE_THRESHOLD = 10;  // Impostato come valore di default
    private ArrayList<ArrayList<Cell>> fullStage = new ArrayList<>(); //Contenido de stage (cuando hemos obtenido un número STAGE_THRESHOLD de stageMarkers)
    private ArrayList<JsonObject> fullStagesDocument = new ArrayList<>(); //Contenido completo del documento con etapas
    private JsonArray fullNoStagesDocument = new JsonArray(); //Contenido completo del documento sin etapas
    private int stageCounter = 1;
    private boolean isThresholdSet = false;  // Flag per verificare se il threshold è già stato impostato
    private int cellCount = 0;  // Contador con el número de celdas del documento





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        resetFiles(); // Reset files on app launch
        Button button = findViewById(R.id.mapViewInformationButton);
        this.telephonyData = new TelephonyData(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isThresholdSet) {
                    showThresholdDialog();
                } else {
                    resetTelephonyData();
                    addMarkerAtCurrentLocation();  // Questo aggiungerà anche il marker e incrementerà il markerCount
                    fetchCellLocation();  // Fetch API data when activity starts
                    addCellsToFile();
                }
            }
        });
    }

    private void resetTelephonyData(){
        this.telephonyData = new TelephonyData(this);
    }

    private void showThresholdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione la cantidad de marcadores por etapa:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!input.getText().toString().isEmpty()) {
                    STAGE_THRESHOLD = Integer.parseInt(input.getText().toString());
                    isThresholdSet = true;
                    Toast.makeText(mapView.this, "Establecido en: " + STAGE_THRESHOLD, Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void addCellsToFile() {

        if (!isExternalStorageWritable()) {
            Log.e("Storage Error", "External Storage is not writable");
            return;
        }

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "comovapp");
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("File Error", "Failed to create directory");
            return;
        }

        String fileName = "jsonCoMov.json";
        File file = new File(storageDir, fileName);
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        ArrayList<Cell> stageData = new ArrayList<>();
        //int counter = 0;
        for (Cell cell : telephonyData.getCells()) {
            jsonObject.add("cell " + cellCount++, gson.toJsonTree(cell).getAsJsonObject());
            fullNoStagesDocument.add(jsonObject);
            jsonObject = new JsonObject();
            stageData.add(cell);
        }
        try (FileWriter writer = new FileWriter(file)) {
            //writer.append(gson.toJson(jsonObject));
            writer.write(gson.toJson(fullNoStagesDocument));
            writer.close();
            Log.d("File Success", "Data written successfully to file");
        } catch (IOException e) {
            Log.e("File I/O Error", Objects.requireNonNull(e.getMessage()));
        }
        fullStage.add(stageData);
        buttonPressCount++;
        if (buttonPressCount >= STAGE_THRESHOLD) {
            saveStageData();
            buttonPressCount = 0; // Reset the counter
            fullStage.clear();
        }
    }

    private void saveStageData() {
        // Ensures directory exists and is ready for file operations
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "comovapp");
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            Log.e("File Error", "Failed to create directory");
            return;
        }

        // Define the file for stage data
        String stageFileName = "stagesData.json";
        File stageFile = new File(storageDir, "stagesData.json");
        Gson gson = new Gson();


        // Crea un array JSON per le celle di questa tappa.
        JsonObject markerData = new JsonObject();
        JsonObject allMarkersData = new JsonObject();  // Crea un JsonObject per mantenere i dati di tutti i marker
        JsonObject stage = new JsonObject();
        int markerIndex = 1;
        int cellIndex = 1;
        for(ArrayList<Cell> marker: fullStage){
            for(Cell cell: marker){
                markerData.add("cell" + cellIndex++, gson.toJsonTree(cell).getAsJsonObject());
            }
            allMarkersData.add("marker" + markerIndex++, markerData);
            markerData = new JsonObject();
            cellIndex = 1;
        }
        stage.add("stage" + stageCounter++, allMarkersData);
        fullStagesDocument.add(stage);
        Log.e("All markers data", stage.toString());
        try (FileWriter writer = new FileWriter(stageFile)) {
            writer.write(gson.toJson(fullStagesDocument));
            writer.close();
            Log.d("Stage File Success", "Stage data written successfully to " + stageFileName);
        } catch (IOException e) {
            Log.e("Stage File Error", "Error writing to file: " + e.getMessage());
        }
    }
    private void resetFiles() {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "comovapp");
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e("File Error", "Failed to create directory");
                return;
            }
        }

        File jsonFile = new File(storageDir, "jsonCoMov.json");
        File stageFile = new File(storageDir, "stagesData.json");

        // Delete jsonCoMov.json if it exists
        if (jsonFile.exists()) {
            if (!jsonFile.delete()) {
                Log.e("File Error", "Failed to delete jsonCoMov.json");
                return;
            }
        }

        // Delete stagesData.json if it exists
        if (stageFile.exists()) {
            if (!stageFile.delete()) {
                Log.e("File Error", "Failed to delete stagesData.json");
                return;
            }
        }

        try {
            // Recreate the files
            jsonFile.createNewFile();
            stageFile.createNewFile();
        } catch (IOException e) {
            Log.e("File Error", "Error while creating files", e);
        }
    }



    /**
     * Checks if external storage is available for read and write
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void addMarkerAtCurrentLocation() {
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            LatLng currentLocation = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
            int signalStrength = telephonyData.getCelldbm();
            mMap.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(getColorForSignalStrength(signalStrength))));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
    }


    private float getColorForSignalStrength(int signalStrength) {
        if (signalStrength > -75) {
            return BitmapDescriptorFactory.HUE_GREEN;
        } else if (signalStrength > -100) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        } else {
            return BitmapDescriptorFactory.HUE_RED;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showPosition();
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setPadding(0, 0, 0, 100);  // Adjust padding (left, top, right, bottom)

        // Set a marker click listener
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Log.e("Control", "He llegado al sitio: el title es: " + marker.getTitle());
                if (!Objects.equals(marker.getTitle(), "Celda CI:")){
                    // Call function to show dialog
                    marker.setTitle("Información de las celdas");
                    marker.setSnippet(telephonyData.getInfo());
                    showDialog(marker.getTitle(), marker.getSnippet());
                }else{
                    showDialog(marker.getTitle(), marker.getSnippet());
                }

                return true;
            }
        });
    }

    private void showDialog(String title, String snippet) {
        // Create and show the dialog
        new AlertDialog.Builder(mapView.this)
                .setTitle(title)
                .setMessage(snippet)

                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Close the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }



    private void showPosition() {
        // Check permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions to user (pop-up)
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 0);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    /**
     *
     * @param requestCode The request code passed in requestPermissions()
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                // Si tenemos permisos
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPosition();
                    addCellsToFile();
                }
                // Si no los tenemos
                else {
                    Log.e("LocationAction", "Se necesitan permisos para poder usar la aplicacion");
                }
            }
        }
    }

    public void fetchCellLocation() {
        Collection<CellLTE> fourGcells = telephonyData.getRegistered4GCells();
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        for(CellLTE fourGcell : fourGcells) {
            Call<ApiResponse> call = apiService.getCellLocation("1.1", "open", fourGcell.getMCC(), fourGcell.getMNC(), fourGcell.getTAC(), fourGcell.getCI());
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ApiResponse apiResponse = response.body();
                        LatLng cellLocation = new LatLng(apiResponse.data.lat, apiResponse.data.lon);
                        // Adding the marker with a custom icon on the map
                        // Resize the icon
                        Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cell_tower);
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 100, 100, false); // Adjust width and height as needed
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mMap != null) {
                                    mMap.addMarker(new MarkerOptions()
                                            .position(cellLocation)
                                            .title("Celda CI:" + fourGcell.getCI())
                                            .snippet("Lat: " + apiResponse.data.lat + "\nLon: " + apiResponse.data.lon + "\n" + "Cell info:\n\n" + fourGcell.toString())
                                            .icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
                                }
                            }
                        });
                    } else {
                        Log.e("API Error", "Failed to retrieve data");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    Log.e("API Failure", Objects.requireNonNull(t.getMessage()));
                }
            });
        }
    }



}