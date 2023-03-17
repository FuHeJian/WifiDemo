package com.example.wifidemo1.model;

public class MyWifiDevice {

    public String wifiSSID;
    public String wifiBSSID;


    public MyWifiDevice(String wifiSSID, String wifiBSSID) {
        this.wifiSSID = wifiSSID;
        this.wifiBSSID = wifiBSSID;
    }

    @Override
    public String toString() {
        return "wifiSSID =" + wifiSSID + ",wifiBSSID =" + wifiBSSID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        if (obj instanceof MyWifiDevice) {
            MyWifiDevice other = (MyWifiDevice) obj;
            if (wifiSSID != null && wifiSSID.equals(other.wifiSSID)) {
                return true;
            }
        }
        return false;
    }
}
