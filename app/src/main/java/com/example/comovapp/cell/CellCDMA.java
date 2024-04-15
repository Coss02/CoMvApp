package com.example.comovapp.cell;

public class CellCDMA extends Cell{
    private int NID; //Network ID
    private int SystemID; //System ID
    private int BaseStationID; //Base station ID

    public CellCDMA(int NID, int SystemID, int BaseStationID, int RSSI, int LEVEL) {
        super(2, RSSI, LEVEL);
        this.NID = NID;
        this.SystemID = SystemID;
        this.BaseStationID = BaseStationID;
    }

    public int getNID() {
        return NID;
    }

    public void setNID(int NID) {
        this.NID = NID;
    }

    public int getSystemID() {
        return SystemID;
    }

    public void setSystemID(int systemID) {
        SystemID = systemID;
    }

    public int getBaseStationID() {
        return BaseStationID;
    }

    public void setBaseStationID(int baseStationID) {
        BaseStationID = baseStationID;
    }
}
