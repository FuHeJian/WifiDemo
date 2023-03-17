package com.example.wifidemo1.model;

public class SearchTargetSendData {
    @Override
    public String toString() {
        return "SearchTargetSendData{" +
                "levelAngle=" + levelAngle +
                ", verticalAngle=" + verticalAngle +
                ", roll=" + roll +
                '}';
    }

    public float levelAngle;
    public float verticalAngle;
    public float roll;
}
