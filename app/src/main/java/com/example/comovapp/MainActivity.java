package com.example.comovapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.Manifest;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comovapp.cell.Cell;
import com.example.comovapp.cell.CellCDMA;
import com.example.comovapp.cell.CellGSM;
import com.example.comovapp.cell.CellLTE;
import com.example.comovapp.cell.CellNR;
import com.example.comovapp.cell.CellWCDMA;
import com.example.comovapp.terminalinfo.TerminalInfo;

//import org.chromium.net.CronetEngine;

import java.util.ArrayList;
import java.util.Collection;
//import java.util.Iterator;
import java.util.List;


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
    }



    public void openMapActivity(View v)
    {
        Intent intent = new Intent(this, mapView.class);
        startActivity(intent);
    }

    public void openInformationActivity(View v){
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