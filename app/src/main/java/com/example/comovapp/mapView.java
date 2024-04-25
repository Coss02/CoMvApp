package com.example.comovapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.os.Environment;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityNr;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoNr;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
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
import com.google.gson.JsonObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class mapView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TelephonyData telephonyData;
    private ActivityMapViewBinding binding;
    private final int PERMISSION_REQUEST_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Set up the button click listener to add a marker
        Button button = findViewById(R.id.mapViewInformationButton);
        this.telephonyData = new TelephonyData(this);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Imprimir aquí en fichero información de las celdas en JSON
                addMarkerAtCurrentLocation();
                fetchCellLocation(); // Fetch API data when activity starts
                addCellsToFile();

            }
        });
    }

    public void addCellsToFile() {
        Log.d("Function Entry", "addCellsToFile entered successfully");
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        int counter = 1;

        // Ensure the external storage is writable
        if (!isExternalStorageWritable()) {
            Log.e("Storage Error", "External Storage is not writable");
            return;
        }

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "comovapp");
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e("File Error", "Failed to create directory");
                return;
            }
        }

        String fileName = "jsonCoMov.json";
        File file = new File(storageDir, fileName);

        for (Cell cell : telephonyData.getCells()) {
            jsonObject.add("cell" + counter, gson.toJsonTree(cell).getAsJsonObject());
            counter++;
        }
        String jsonString = jsonObject.toString();

        try {
            // Use FileWriter to write to the file
            FileWriter writer = new FileWriter(file, true); // Append mode set to true
            writer.append(jsonString);
            writer.close();
            Log.d("File Success", "Data written successfully to file");
        } catch (IOException e) {
            Log.e("File I/O Error", "Error writing jsonString to file: " + e.getMessage());
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

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
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
                if (!Objects.equals(marker.getTitle(), "Cell Position")){
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
                                    // Ensure you have ic_cell_tower.png in your drawable folder
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