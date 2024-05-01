package com.example.comovapp.cell;

import androidx.annotation.NonNull;

public class CellWCDMA extends Cell{
    private int CID;
    private int LAC;
    private int PSC;

    public CellWCDMA(int CID, int LAC, int PSC,int RSSI, int LEVEL, boolean isRegistered) {
        super(3, RSSI, LEVEL, isRegistered);
        this.CID = CID;
        this.LAC = LAC;
        this.PSC = PSC;
    }

    public int getCID() {
        return CID;
    }

    public void setCID(int CID) {
        this.CID = CID;
    }

    public int getLAC() {
        return LAC;
    }

    public void setLAC(int LAC) {
        this.LAC = LAC;
    }

    public int getPSC() {
        return PSC;
    }

    public void setPSC(int PSC) {
        this.PSC = PSC;
    }

    @NonNull
    public String toString(){
        return "CID: " + CID + "   " +
                "LAC: " + LAC + "\n" +
                "PSC: " + PSC + "   " +
                super.toString();
    }
}
