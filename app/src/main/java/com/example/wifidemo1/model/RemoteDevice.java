package com.example.wifidemo1.model;

import androidx.annotation.NonNull;

import java.util.Objects;


public class RemoteDevice extends BaseModel {

    public String deviceName;

    public RemoteDevice(@NonNull String wifiSSID) {
        this.deviceName = wifiSSID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RemoteDevice)) return false;
        RemoteDevice that = (RemoteDevice) o;
        return Objects.equals(deviceName, that.deviceName);
    }


    @Override
    public String toString() {
        return "wifiSSID= " + deviceName;
    }

}
