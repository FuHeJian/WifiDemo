package com.example.wifidemo1.model;

import androidx.annotation.NonNull;

import com.litesuits.orm.db.annotation.Table;

import java.util.Objects;

@Table("VerificationDevice")
public class VerificationDevice extends BaseModel {

    public String wifiSSID;


    public String devicePassWord;

    public VerificationDevice(@NonNull String wifiSSID, @NonNull String devicePassWord) {
        this.wifiSSID = wifiSSID;
        this.devicePassWord = devicePassWord;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VerificationDevice)) return false;
        VerificationDevice that = (VerificationDevice) o;
        return Objects.equals(wifiSSID, that.wifiSSID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wifiSSID, devicePassWord);
    }

    @Override
    public String toString() {
        return "wifiSSID= " + wifiSSID +
                ", devicePassWord= " + devicePassWord;
    }

}
