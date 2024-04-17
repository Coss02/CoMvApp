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
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthNr;
import android.telephony.CellSignalStrengthWcdma;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.comovapp.cell.Cell;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.comovapp.databinding.ActivityMapViewBinding;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

public class mapView extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Imprimir aquí en fichero información de las celdas en JSON
                addCellsToFile();
                addMarkerAtCurrentLocation();
            }
        });
    }

    public void addCellsToFile(){
        JSONObject j = new JSONObject();
        JSONArray cellArray = new JSONArray();
        // Collection<Cell> cells = MainActivity.showCellInfo(); Hace falta ver cómo leemos las celdas aquí

    }

    private void addMarkerAtCurrentLocation() {
        if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getMyLocation();
            LatLng currentLocation = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
            mMap.addMarker(new MarkerOptions().position(currentLocation).title("You are here"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showPosition();

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

}