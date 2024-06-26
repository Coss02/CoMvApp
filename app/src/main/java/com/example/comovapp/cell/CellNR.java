package com.example.comovapp.cell;

import androidx.annotation.NonNull;

public class CellNR extends Cell{
    private int NCI;
    private String MCC;
    private String MNC;
    private int TAC;


    public CellNR( int NCI, String MCC, String MNC, int TAC, int RSSI, int LEVEL, boolean isRegistered) {
        super(5, RSSI, LEVEL, isRegistered);
        this.NCI = NCI;
        this.MCC = MCC;
        this.MNC = MNC;
        this.TAC = TAC;
    }

    public int getNCI() {
        return NCI;
    }

    public void setNCI(int NCI) {
        this.NCI = NCI;
    }

    public String getMCC() {
        return MCC;
    }

    public void setMCC(String MCC) {
        this.MCC = MCC;
    }

    public String getMNC() {
        return MNC;
    }

    public void setMNC(String MNC) {
        this.MNC = MNC;
    }

    public int getTAC() {
        return TAC;
    }

    public void setTAC(int TAC) {
        this.TAC = TAC;
    }

    @NonNull
    public String toString(){
        return "NCI: " + NCI + "   " +
                "MCC: " + MCC + "   " +
                "MNC: " + MNC + "\n" +
                "TAC: " + TAC + "\n" +
                super.toString();
    }
}
