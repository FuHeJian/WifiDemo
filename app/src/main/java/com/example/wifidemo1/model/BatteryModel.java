package com.example.wifidemo1.model;

import java.io.Serializable;

public class BatteryModel implements Serializable {


    public int capacity;
    public int charge;//0=不充电，1=充电中；2=充满
    @Override
    public String toString() {
        return "BatteryModel{" +
                "capacity='" + capacity + '\'' +
                ", charge='" + charge + '\'' +
                '}';
    }

}
