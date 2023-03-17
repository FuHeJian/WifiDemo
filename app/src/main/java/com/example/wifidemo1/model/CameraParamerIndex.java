package com.example.wifidemo1.model;

import com.litesuits.orm.db.annotation.Table;

@Table("CameraParamerIndex")
public class CameraParamerIndex extends BaseModel {
    public int index;
    public String value;
    public int extraValue;

    public CameraParamerIndex(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public CameraParamerIndex(int index, String value, int extraValue) {
        this.index = index;
        this.value = value;
        this.extraValue = extraValue;
    }

    @Override
    public String toString() {
        return "CameraParamerIndex{" +
                "index=" + index +
                ", value='" + value + '\'' +
                ", extraValue=" + extraValue +
                '}';
    }

    public static CameraParamerIndex copy(int index, String value){
        return new CameraParamerIndex(index, value);
    }

    public static CameraParamerIndex copy(int index, String value, int extraValue){
        return new CameraParamerIndex(index, value,extraValue);
    }
}
