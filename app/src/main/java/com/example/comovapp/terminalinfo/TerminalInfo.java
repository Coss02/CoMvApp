package com.example.comovapp.terminalinfo;

public class TerminalInfo {
    private int VoiceNetworkType;
    private int DataNetworkType;
    private String NetworkOperatorName;

    public TerminalInfo(int voiceNetworkType, int dataNetworkType, String networkOperatorName) {
        VoiceNetworkType = voiceNetworkType;
        DataNetworkType = dataNetworkType;
        NetworkOperatorName = networkOperatorName;
    }

    public TerminalInfo() {
    }
    public int getVoiceNetworkType() {
        return VoiceNetworkType;
    }

    public void setVoiceNetworkType(int voiceNetworkType) {
        VoiceNetworkType = voiceNetworkType;
    }

    public int getDataNetworkType() {
        return DataNetworkType;
    }

    public void setDataNetworkType(int dataNetworkType) {
        DataNetworkType = dataNetworkType;
    }

    public String getNetworkOperatorName() {
        return NetworkOperatorName;
    }

    public void setNetworkOperatorName(String networkOperatorName) {
        NetworkOperatorName = networkOperatorName;
    }
}
