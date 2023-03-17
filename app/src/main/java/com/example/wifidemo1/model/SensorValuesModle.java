package com.example.wifidemo1.model;

import java.io.Serializable;
import java.util.Arrays;

public class SensorValuesModle implements Serializable {

    private float[] values;
    private double compass;
    private double alt;

    public SensorValuesModle(float[] values, double compass, double alt) {
        this.values = values;
        this.compass = compass;
        this.alt = alt;
    }

    public float[] getValues() {
        return values;
    }

    public double getCompass() {
        return compass;
    }

    public double getAlt() {
        return alt;
    }

    @Override
    public String toString() {
        return "SensorValuesModle{" +
                "values=" + Arrays.toString(values) +
                ", compass=" + compass +
                ", alt=" + alt +
                '}';
    }
}
