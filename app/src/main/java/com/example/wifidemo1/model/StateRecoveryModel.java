package com.example.wifidemo1.model;

public class StateRecoveryModel {
    public int mode = 1;
    public int state = 0;
    public int remNum = 0;
    public int runTime = 0;
    public int remTime = 0;
    public String track  ;
    public int speed = 0;
    public int halfSpeed = 0;
    public  String startTime;
    public  String endTime;
    public  int interval;
    public  boolean sunRise;
    public int photoNum; //已拍张数

    @Override
    public String toString() {
        return "StateRecoveryModel{" +
                "mode=" + mode +
                ", state=" + state +
                ", remNum=" + remNum +
                ", runTime=" + runTime +
                ", remTime=" + remTime +
                ", track='" + track + '\'' +
                ", speed=" + speed +
                ", halfSpeed=" + halfSpeed +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", interval=" + interval +
                ", sunRise=" + sunRise +
                ", photoNum=" + photoNum +
                '}';
    }
}
