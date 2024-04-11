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
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private TelephonyManager telephonyManager;



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
        //Iniciamos TelephonyManager
        this.telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
    }


    public void openMapActivity(View v)
    {
        Intent intent = new Intent(this, mapView.class);
        startActivity(intent);
    }

    public void openInformationActivity(View v){
        String text = "";

        text += showTerminalInfo();
        text += showCellInfo();

        TextView textView = findViewById(R.id.showInfoText);
        textView.setText(text);
    }

    public String showTerminalInfo() {
        // Chequeamos permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                Log.i("TelephonyActivity", "Should show rationale");
            } else {
                ActivityCompat.requestPermissions(this, new String[]
                        {
                                Manifest.permission.READ_PHONE_STATE
                        }, 0);
            }
            return "";
        }

        String text = "";
        // Obtenemos el tipo de red para comunicaciones de voz
        text += "Voice Network Type: " + this.telephonyManager.getVoiceNetworkType() + "\n";
        // Obtenemos el tipo de red para comunicaciones de datos
        text += "Data Network Type: " + this.telephonyManager.getDataNetworkType() + "\n";
        // Obtenemos el nombre del operador de red
        text += "Network Operator Name: " + this.telephonyManager.getNetworkOperatorName() + "\n";
        return text;
    }

    public String showCellInfo() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i("TelephonyActivity", "Should show rationale");
            } else {
                ActivityCompat.requestPermissions(this, new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, 1);
            }
            return "";
        }
        String text = "";
        List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoGsm) { //2G
                CellSignalStrengthGsm signalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                CellIdentityGsm identityGsm = ((CellInfoGsm) cellInfo).getCellIdentity();
                // Concatenate GSM-specific info
                text += "GSM Cell (2G): " + "CID: " + identityGsm.getCid() + ", LAC: " + identityGsm.getLac() + ", RSSI: " + signalStrengthGsm.getDbm() + " dBm\n";
            } else if (cellInfo instanceof CellInfoCdma) { //2G
                CellSignalStrengthCdma signalStrengthCdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                CellIdentityCdma identityCdma = ((CellInfoCdma) cellInfo).getCellIdentity();
                // Concatenate CDMA-specific info
                text += "CDMA Cell (2G): " + "Network ID: " + identityCdma.getNetworkId() + ", System ID: " + identityCdma.getSystemId() + ", Base Station ID: " + identityCdma.getBasestationId() + ", RSSI: " + signalStrengthCdma.getDbm() + " dBm\n";
            }  else if (cellInfo instanceof CellInfoWcdma) { //3G
                CellSignalStrengthWcdma signalStrengthWcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                CellIdentityWcdma identityWcdma = ((CellInfoWcdma) cellInfo).getCellIdentity();
                // Concatenate WCDMA-specific info
                text += "WCDMA Cell (3G): " + "LAC: " + identityWcdma.getLac() + ", CID: " + identityWcdma.getCid() + ", PSC: " + identityWcdma.getPsc() + ", RSSI: " + signalStrengthWcdma.getDbm() + " dBm\n";
            }else if (cellInfo instanceof CellInfoLte) { //4G
                CellSignalStrengthLte signalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                CellIdentityLte identityLte = ((CellInfoLte) cellInfo).getCellIdentity();
                // Concatenate LTE-specific info
                text += "LTE Cell (4G): " + "CI: " + identityLte.getCi() + ", TAC: " + identityLte.getTac() + ", PCI: " + identityLte.getPci() + ", RSSI: " + signalStrengthLte.getDbm() + " dBm" + ", LEVEL: " + signalStrengthLte.getLevel() + "\n";
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //5G
                if (cellInfo instanceof CellInfoNr) {
                    // 5G NR information concatenation would go here - Requires API 29 and above
                    // Since 5G specifics are more complex and API dependent, we'll keep this generic
                    CellInfoNr cellInfoNr = (CellInfoNr) cellInfo;
                    CellIdentityNr id = (CellIdentityNr) cellInfoNr.getCellIdentity();
                    CellSignalStrengthNr signalStrengthNr = (CellSignalStrengthNr) ((CellInfoNr) cellInfo).getCellSignalStrength();
                    text += "NR Cell (5G): CID: " + id.getNci() + ", MCC: " + id.getMccString() + ", MNC: " + id.getMncString() + ", TAC: " + id.getTac() +  ", SSRSRP: " + signalStrengthNr.getSsRsrp() + " dBm" +", LEVEL: " + cellInfoNr.getCellSignalStrength().getLevel() + "\n";
                }
            }


        }
        return text;
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
                    showTerminalInfo();
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
                    showCellInfo();
                }
                else {
                    Log.e("LocationAction", "Permisos de acceso a datos de celda del terminal DENEGADOS");
                }
            }
        }
    }
}