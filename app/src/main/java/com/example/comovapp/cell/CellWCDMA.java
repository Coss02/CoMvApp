package com.example.comovapp.cell;

public class CellWCDMA extends Cell{
    private int CID;
    private int LAC;
    private int PSC;

    public CellWCDMA(int RSSI, int LEVEL, int CID, int LAC, int PSC) {
        super(3, RSSI, LEVEL);
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
}
