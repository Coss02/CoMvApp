package com.example.comovapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

//import org.chromium.net.CronetEngine;

//import java.util.Iterator;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    private TelephonyData telephonyData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.telephonyData = new TelephonyData(this);
        ImageButton it = findViewById(R.id.italianButton);
        ImageButton es = findViewById(R.id.spanishButton);

        it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage("it");
            }
        });
        es.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLanguage("es");
            }
        });
    }
    private void changeLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);

        // Update the locale for the application context
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        TextView showInfoButton = findViewById(R.id.showInformationButt);
        TextView showMapButton = findViewById(R.id.showMapButt);
        TextView titleButton = findViewById(R.id.titleText);
        TextView mapViewButton = findViewById(R.id.mapViewInformationButton);
        showInfoButton.setText(R.string.mostrar_informacion2);
        showMapButton.setText(R.string.mostrar_mapa);
        titleButton.setText(R.string.bienvenido);
        mapViewButton.setText(R.string.mostrar_mapa);

    }

    private void resetTelephonyData(){
        this.telephonyData = new TelephonyData(this);
    }

    public void openMapActivity(View v)
    {
        Intent intent = new Intent(this, mapView.class);
        startActivity(intent);
    }

    public void openInformationActivity(View v){
        resetTelephonyData();
        TextView textView = findViewById(R.id.showInfoText);
        textView.setText(String.format(telephonyData.getInfo()));
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            // Request permissions for ShowTerminalInfo()
            case 0:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("LocationAction", "Permisos de acceso a datos de telefonia del terminal concedidos");
                    
                }
                else {
                    Log.e("LocationAction", "Permisos de acceso a datos de telefonia del terminal DENEGADOS");
                }
            }
            case 1:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Log.d("LocationAction", "Permisos de acceso a datos de celda del terminal concedidos");
                }
                else {
                    Log.e("LocationAction", "Permisos de acceso a datos de celda del terminal DENEGADOS");
                }
            }
        }

    }
}