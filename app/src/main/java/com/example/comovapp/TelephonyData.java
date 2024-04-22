package com.example.comovapp;

import static android.content.Context.TELEPHONY_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.comovapp.cell.Cell;
import com.example.comovapp.cell.CellCDMA;
import com.example.comovapp.cell.CellGSM;
import com.example.comovapp.cell.CellLTE;
import com.example.comovapp.cell.CellNR;
import com.example.comovapp.cell.CellWCDMA;
import com.example.comovapp.terminalinfo.TerminalInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TelephonyData {
    private final TelephonyManager telephonyManager; //telephonyManger
    private final Activity activity; //Actividad que requiere el método

    private Collection<Cell> cells; //Celdas encontradas
    private TerminalInfo terminalInfo; //Información del teléfono

    private String info; //Toda la información formateada en un String

    public TelephonyData(Activity activity) {
        this.activity = activity;
        this.telephonyManager = (TelephonyManager) activity.getSystemService(TELEPHONY_SERVICE);
        this.cells = showCellInfo();
        this.terminalInfo = showTerminalInfo();
        this.info = telephonyInfo();
    }

    private Collection<Cell> showCellInfo() {
        Collection<Cell> cells = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this.activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.i("TelephonyActivity", "Should show rationale");
            } else {
                ActivityCompat.requestPermissions(this.activity, new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, 1);
            }
            return cells;
        }
        //String text = "";
        List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();
        for (CellInfo cellInfo : cellInfoList) {
            if (cellInfo instanceof CellInfoGsm) { //2G
                CellSignalStrengthGsm signalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                CellIdentityGsm identityGsm = ((CellInfoGsm) cellInfo).getCellIdentity();
                // Concatenate GSM-specific info
                //text += "GSM Cell (2G): " + "CID: " + identityGsm.getCid() + ", LAC: " + identityGsm.getLac() + ", RSSI: " + signalStrengthGsm.getDbm() + " dBm\n";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    CellGSM cellGSM = new CellGSM(identityGsm.getCid(), identityGsm.getLac(), signalStrengthGsm.getRssi(), signalStrengthGsm.getLevel());
                    cells.add(cellGSM);
                } else {
                    CellGSM cellGSM = new CellGSM(identityGsm.getCid(), identityGsm.getLac(), signalStrengthGsm.getLevel());
                    cells.add(cellGSM);
                }
            } else if (cellInfo instanceof CellInfoCdma) { //2G
                CellSignalStrengthCdma signalStrengthCdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                CellIdentityCdma identityCdma = ((CellInfoCdma) cellInfo).getCellIdentity();
                // Concatenate CDMA-specific info
                //text += "CDMA Cell (2G): " + "Network ID: " + identityCdma.getNetworkId() + ", System ID: " + identityCdma.getSystemId() + ", Base Station ID: " + identityCdma.getBasestationId() + ", RSSI: " + signalStrengthCdma.getDbm() + " dBm\n";
                CellCDMA cellCDMA = new CellCDMA(identityCdma.getNetworkId(), identityCdma.getSystemId(), identityCdma.getBasestationId(), signalStrengthCdma.getDbm(), signalStrengthCdma.getLevel());
                cells.add(cellCDMA);
            } else if (cellInfo instanceof CellInfoWcdma) { //3G
                CellSignalStrengthWcdma signalStrengthWcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                CellIdentityWcdma identityWcdma = ((CellInfoWcdma) cellInfo).getCellIdentity();
                // Concatenate WCDMA-specific info
                //text += "WCDMA Cell (3G): " + "LAC: " + identityWcdma.getLac() + ", CID: " + identityWcdma.getCid() + ", PSC: " + identityWcdma.getPsc() + ", RSSI: " + signalStrengthWcdma.getDbm() + " dBm\n";
                CellWCDMA cellWCDMA = new CellWCDMA(identityWcdma.getCid(), identityWcdma.getLac(), identityWcdma.getPsc(), signalStrengthWcdma.getDbm(), signalStrengthWcdma.getLevel());
                cells.add(cellWCDMA);
            } else if (cellInfo instanceof CellInfoLte) { //4G
                CellSignalStrengthLte signalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                CellIdentityLte identityLte = ((CellInfoLte) cellInfo).getCellIdentity();
                // Concatenate LTE-specific info
                //text += "LTE Cell (4G): " + "CI: " + identityLte.getCi() + ", TAC: " + identityLte.getTac() + ", PCI: " + identityLte.getPci() + ", RSSI: " + signalStrengthLte.getDbm() + " dBm" + ", LEVEL: " + signalStrengthLte.getLevel() + "\n";
                CellLTE cellLTE = new CellLTE(identityLte.getCi(), identityLte.getMcc(), identityLte.getMnc(), identityLte.getTac(), identityLte.getPci(), signalStrengthLte.getDbm(), signalStrengthLte.getLevel());
                cells.add(cellLTE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { //5G
                if (cellInfo instanceof CellInfoNr) {
                    // 5G NR information concatenation would go here - Requires API 29 and above
                    // Since 5G specifics are more complex and API dependent, we'll keep this generic
                    CellInfoNr cellInfoNr = (CellInfoNr) cellInfo;
                    CellIdentityNr id = (CellIdentityNr) cellInfoNr.getCellIdentity();
                    CellSignalStrengthNr signalStrengthNr = (CellSignalStrengthNr) ((CellInfoNr) cellInfo).getCellSignalStrength();
                    //text += "NR Cell (5G): NCI: " + id.getNci() + ", MCC: " + id.getMccString() + ", MNC: " + id.getMncString() + ", TAC: " + id.getTac() +  ", RSSI:"+ cellInfoNr.getCellSignalStrength().getDbm() + " dBm" +", LEVEL: " + cellInfoNr.getCellSignalStrength().getLevel() + "\n";
                    CellNR cellNR = new CellNR((int) id.getNci(), id.getMccString(), id.getMncString(), id.getTac(), cellInfoNr.getCellSignalStrength().getDbm(), cellInfoNr.getCellSignalStrength().getLevel());
                    cells.add(cellNR);
                }
            }


        }
        return cells;
    }

    private TerminalInfo showTerminalInfo() {
        TerminalInfo terInfo = new TerminalInfo();
        // Chequeamos permisos
        if (ActivityCompat.checkSelfPermission(this.activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.activity, Manifest.permission.READ_PHONE_STATE)) {
                Log.i("TelephonyActivity", "Should show rationale");
            } else {
                ActivityCompat.requestPermissions(this.activity, new String[]
                        {
                                Manifest.permission.READ_PHONE_STATE
                        }, 0);
            }
            return terInfo;
        }
        // Obtenemos el tipo de red para comunicaciones de voz
        terInfo.setVoiceNetworkType(this.telephonyManager.getVoiceNetworkType());
        // Obtenemos el tipo de red para comunicaciones de datos
        terInfo.setDataNetworkType(this.telephonyManager.getDataNetworkType());
        // Obtenemos el nombre del operador de red
        terInfo.setNetworkOperatorName(this.telephonyManager.getNetworkOperatorName());

        return terInfo;
    }

    private String telephonyInfo() {
        return "Voice Network Type: " + this.terminalInfo.getVoiceNetworkType() + "\n" +
                "Data Network Type: " + this.terminalInfo.getDataNetworkType() + "\n" +
                "Network Operator Name: " + this.telephonyManager.getNetworkOperatorName() + "\n" +
                "\n" +
                cellInfo() + "\n";
    }

    private String cellInfo() {
        StringBuilder info = new StringBuilder();
        for (Cell cell : cells) {
            info.append(cell.toString()).append("\n");
        }
        return info.toString();
    }

    public TelephonyManager getTelephonyManager() {
        return telephonyManager;
    }

    public Activity getActivity() {
        return activity;
    }

    public Collection<Cell> getCells() {
        return cells;
    }

    public void setCells(Collection<Cell> cells) {
        this.cells = cells;
    }

    public TerminalInfo getTerminalInfo() {
        return terminalInfo;
    }

    public void setTerminalInfo(TerminalInfo terminalInfo) {
        this.terminalInfo = terminalInfo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getCelldbm() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return 0;
        }

        for (CellInfo cellInfo : this.telephonyManager.getAllCellInfo()) {
            if (cellInfo.isRegistered()) {
                CellSignalStrength signalStrength = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    signalStrength = cellInfo.getCellSignalStrength();
                }
                assert signalStrength != null;
                return signalStrength.getDbm();  // dBm values
            }
        }
        return 0;  // default or no signal
    }

}
