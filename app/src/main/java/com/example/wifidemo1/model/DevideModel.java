package com.example.wifidemo1.model;

public class DevideModel {
    public String deviceName;
    public String devicePassword;
    public String imei;


    public boolean visiblePassword;
    public DevideModel(String deviceName, String devicePassword, boolean visiblePassword, String imei) {
        this.deviceName = deviceName;
        this.devicePassword = devicePassword;
        this.visiblePassword = visiblePassword;
        this.imei = imei;
    }
    @Override
    public String toString() {
        return "DevideModel{" +
                "deviceName='" + deviceName + '\'' +
                ", devicePassword='" + devicePassword + '\'' +
                ", visiblePassword=" + visiblePassword +
                '}';
    }

}
