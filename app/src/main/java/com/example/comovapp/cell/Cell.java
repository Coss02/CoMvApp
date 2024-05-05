package com.example.comovapp.cell;

import androidx.annotation.NonNull;

public class Cell {
    private int Gen; //Generación (5G,2G,etc)
    private int RSSI; //Potencia de señal recibida
    private int LEVEL;
    private boolean isRegistered; // Se está usando la celda o no

    public Cell(int Gen, int RSSI, int LEVEL, boolean isRegistered) {
        this.Gen = Gen;
        this.RSSI = RSSI;
        this.LEVEL = LEVEL;
        this.isRegistered = isRegistered;
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

    public boolean isRegistered() {
        return isRegistered;
    }

    public void setRegistered(boolean registered) {
        isRegistered = registered;
    }

    @NonNull
    public String toString(){
        return "Celda " + Gen + "G   " + "RSSI: " + RSSI + "   " + "LEVEL: " + LEVEL +  "\n" + "Registered Cell: " + isRegistered + "\n";
    }
}
