package com.example.wifidemo1.model;

import java.io.Serializable;

public class RecordStateModel implements Serializable {
    public int code;
    public String recordState;
    public String ret;
    public int remainNum;
    public int appointmentStateStart; // ret:1=预约中，2=运行中；0=结束；
    public int sendPointIndex;
    public int photoNum;

    @Override
    public String toString() {
        return "RecordStateModel{" +
                "code=" + code +
                ", recordState='" + recordState + '\'' +
                ", ret='" + ret + '\'' +
                ", remainNum=" + remainNum +
                ", appointmentStateStart=" + appointmentStateStart +
                ", sendPointIndex=" + sendPointIndex +
                ", photoNum=" + photoNum +
                '}';
    }
}
