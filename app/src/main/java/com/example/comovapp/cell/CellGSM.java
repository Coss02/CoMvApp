package com.example.comovapp.cell;

public class CellGSM extends Cell {
    private int CID;
    private int LAC;

    public CellGSM(int CID, int LAC, int RSSI, int LEVEL) {
        super(2, RSSI, LEVEL);
        this.CID = CID;
        this.LAC = LAC;
    }
    public CellGSM(int CID, int LAC, int LEVEL) {
        super(2, 9999, LEVEL); //9999 como error del RSSI
        this.CID = CID;
        this.LAC = LAC;
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
}
