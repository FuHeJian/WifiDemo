package com.example.wifidemo1.model;

public class ShootingModel {
    private String stringID;
    private int selectImageID;
    private int unSelectImageID;

    private int shootingModel;

    public ShootingModel(String stringID, int selectImageID, int unSelectImageID, int shootingModel) {
        this.stringID = stringID;
        this.selectImageID = selectImageID;
        this.unSelectImageID = unSelectImageID;
        this.shootingModel = shootingModel;
    }

    public String getStringID() {
        return stringID;
    }

    public void setStringID(String stringID) {
        this.stringID = stringID;
    }

    public int getSelectImageID() {
        return selectImageID;
    }

    public void setSelectImageID(int selectImageID) {
        this.selectImageID = selectImageID;
    }

    public int getUnSelectImageID() {
        return unSelectImageID;
    }

    public void setUnSelectImageID(int unSelectImageID) {
        this.unSelectImageID = unSelectImageID;
    }


    public int getShootingModel() {
        return shootingModel;
    }

    public void setShootingModel(int shootingModel) {
        this.shootingModel = shootingModel;
    }

}
