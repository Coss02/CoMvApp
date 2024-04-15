package com.example.comovapp.cell;

public class CellNR extends Cell{
    private int NCI;
    private int MCC;
    private int MNC;
    private int TAC;


    public CellNR( int RSSI, int LEVEL, int NCI, int MCC, int MNC, int TAC) {
        super(5, RSSI, LEVEL);
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

    public int getTAC() {
        return TAC;
    }

    public void setTAC(int TAC) {
        this.TAC = TAC;
    }
}
