package com.example.comovapp.cell;

import android.telephony.CellInfo;

import androidx.annotation.NonNull;

public class CellLTE extends Cell{
    private int CI;
    private int MCC;
    private int MNC;
    private int TAC;
    private int PCI;

    private static boolean isRegistered;

    public CellLTE( int CI, int MCC, int MNC, int TAC, int PCI, boolean Registered, int RSSI, int LEVEL) {
        super(4, RSSI, LEVEL);
        this.CI = CI;
        this.MCC = MCC;
        this.MNC = MNC;
        this.TAC = TAC;
        this.PCI = PCI;
        isRegistered = Registered;
    }

    public int getCI() {
        return CI;
    }

    public void setCI(int CI) {
        this.CI = CI;
    }

    public int getTAC() {
        return TAC;
    }

    public void setTAC(int TAC) {
        this.TAC = TAC;
    }

    public int getPCI() {
        return PCI;
    }

    public void setPCI(int PCI) {
        this.PCI = PCI;
    }

    public int getMCC() {
        return MCC;
    }

    public void setMCC(int MCC) {
        this.MCC = MCC;
    }

    public int getMNC() {
        return MNC;
    }

    public void setMNC(int MNC) {
        this.MNC = MNC;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    @NonNull
    public String toString(){
        return "CI: " + CI + "   " +
                "PCI: " + PCI + "\n" +
                "MCC: " + MCC + "   " +
                "MNC: " + MNC + "\n" +
                "TAC: " + TAC + "\n" +
                super.toString();
    }
}
