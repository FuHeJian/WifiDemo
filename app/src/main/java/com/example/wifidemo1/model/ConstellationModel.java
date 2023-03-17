package com.example.wifidemo1.model;

public class ConstellationModel extends BaseModel{
    private String name;
    private String describe;
    private String rises;
    private String sets;
    private String azimuth;
    private String Altuitude;
    private String rightAscension;
    private String declination;
    private int imageID;
    private boolean canSeclect;

    public ConstellationModel(String name, String describe, String rises, String sets, String azimuth, String altuitude, String rightAscension, String declination, int imageID, boolean canSeclect) {
        this.name = name;
        this.describe = describe;
        this.rises = rises;
        this.sets = sets;
        this.azimuth = azimuth;
        Altuitude = altuitude;
        this.rightAscension = rightAscension;
        this.declination = declination;
        this.imageID = imageID;
        this.canSeclect = canSeclect;
    }


    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getRises() {
        return rises;
    }

    public void setRises(String rises) {
        this.rises = rises;
    }

    public String getSets() {
        return sets;
    }

    public void setSets(String sets) {
        this.sets = sets;
    }

    public String getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(String azimuth) {
        this.azimuth = azimuth;
    }

    public String getAltuitude() {
        return Altuitude;
    }

    public void setAltuitude(String altuitude) {
        Altuitude = altuitude;
    }

    public String getRightAscension() {
        return rightAscension;
    }

    public void setRightAscension(String rightAscension) {
        this.rightAscension = rightAscension;
    }

    public String getDeclination() {
        return declination;
    }

    public void setDeclination(String declination) {
        this.declination = declination;
    }

    public boolean isCanSeclect() {
        return canSeclect;
    }

    public void setCanSeclect(boolean canSeclect) {
        this.canSeclect = canSeclect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

}