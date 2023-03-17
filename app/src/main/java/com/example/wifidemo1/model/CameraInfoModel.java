package com.example.wifidemo1.model;

import java.util.List;

public class CameraInfoModel {

    private boolean available;

    public boolean isCameraHasBuld() {
        return cameraHasBuld;
    }

    public void setCameraHasBuld(boolean cameraHasBuld) {
        this.cameraHasBuld = cameraHasBuld;
    }

    private boolean cameraHasBuld;
    private int selectIndex;

    private List<CameraParamerIndex> infoList;


    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }


    public List<CameraParamerIndex> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<CameraParamerIndex> infoList) {
        this.infoList = infoList;
    }

    @Override
    public String toString() {
        return "CameraInfoModel{" +
                "available=" + available +
                ", index=" + selectIndex +
                '}';
    }

}
