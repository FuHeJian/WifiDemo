package com.example.wifidemo1.model;

import android.bluetooth.BluetoothDevice;

import java.util.Objects;


public class PolarisDevice extends BaseModel {

    public String deviceName;
    public String bssid;
    public BluetoothDevice bluetoothDevice;
    public boolean remote;
    public boolean online;

    public PolarisDevice(String deviceName) {
        this.deviceName = deviceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolarisDevice)) return false;
        PolarisDevice that = (PolarisDevice) o;

        return Objects.equals(deviceName, that.deviceName);
    }

    @Override
    public String toString() {
        return "---deviceName= " + deviceName + ",bluetoothDevice= " + bluetoothDevice + ",remote =" + remote + ",online =" + online+",bssid ="+bssid;
    }
}
