package com.example.comovapp;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.comovapp.databinding.ActivityMapViewBinding;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class mapView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private TelephonyManager telephonyManager;
    private ActivityMapViewBinding binding;


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
        this.telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Imprimir aquí en fichero información de las celdas en JSON
                addCellsToFile();
                addMarkerAtCurrentLocation();
                fetchCellLocation(); // Fetch API data when activity starts

            }
        });
    }

    public void addCellsToFile() {
        JSONObject j = new JSONObject();
        JSONArray cellArray = new JSONArray();
        // Collection<Cell> cells = MainActivity.showCellInfo(); Hace falta ver cómo leemos las celdas aquí

    }
    private void addMarkerAtCurrentLocation() {
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            LatLng currentLocation = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
            int signalStrength = getSignalStrength();
            mMap.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .title("You are here")
                    .flat(true)
                    .icon(BitmapDescriptorFactory.defaultMarker(getColorForSignalStrength(signalStrength))));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
    }

    private int getSignalStrength() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }
        List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo.isRegistered()) {
                CellSignalStrength signalStrength = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    signalStrength = cellInfo.getCellSignalStrength();
                }
                return signalStrength.getDbm();  // dBm values
            }
        }
        return 0;  // default or no signal
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
            public boolean onMarkerClick(Marker marker) {
                // Call function to show dialog
                marker.setTitle("Información Avanzada");
                showDialog(marker.getTitle(), marker.getSnippet());
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
                }
                // Si no los tenemos
                else {
                    Log.e("LocationAction", "Se necesitan permisos para poder usar la aplicacion");
                }
            }
        }
    }

    public void fetchCellLocation() {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        Call<ApiResponse> call = apiService.getCellLocation("1.1", "open", 250, 2, 7840, 200719106L);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
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
                                        .title("Cell Position")
                                        .snippet("Lat: " + apiResponse.data.lat + ", Lon: " + apiResponse.data.lon)
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
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e("API Failure", t.getMessage());
            }
        });
    }



}