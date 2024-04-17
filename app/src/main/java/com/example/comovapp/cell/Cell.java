package com.example.comovapp.cell;

public class Cell {
    private int Gen; //Generación (5G,2G,etc)
    private int RSSI; //fuerza de señal recibida
    private int LEVEL;

    public Cell(int Gen, int RSSI, int LEVEL) {
        this.Gen = Gen;
        this.RSSI = RSSI;
        this.LEVEL = LEVEL;
    }
    public Cell(){

    }

    public int getGen() {
        return Gen;
    }

    public void setGen(int gen) {
        Gen = gen;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public int getLEVEL() {
        return LEVEL;
    }

    public void setLEVEL(int LEVEL) {
        this.LEVEL = LEVEL;
    }
}
