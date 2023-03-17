package com.example.wifidemo1.model;

import android.graphics.Bitmap;

public class DynamiclapseModel {
    private Bitmap bitmap;
    private DynamiclapsePoint point;

    private float interval;
    private int pictureCount;


    public DynamiclapseModel( float interval,int pictureCount) {

        this.interval =interval;
        this.pictureCount =pictureCount;
    }

    public DynamiclapseModel(Bitmap bitmap, DynamiclapsePoint point, int interval) {
        this.bitmap =bitmap;
        this.point =point;
        this.interval =interval;
    }


    public DynamiclapseModel(Bitmap bitmap, DynamiclapsePoint point ) {
        this.bitmap =bitmap;
        this.point =point;

    }

    public DynamiclapseModel(Bitmap bitmap, DynamiclapsePoint point, float interval,int pictureCount) {
        this.bitmap =bitmap;
        this.point =point;
        this.interval =interval;
        this.pictureCount =pictureCount;
    }


    public float getInterval() {
        return interval;
    }

    public void setInterval(float interval) {
        this.interval = interval;
    }

    public int getPictureCount() {
        return pictureCount;
    }

    public void setPictureCount(int pictureCount) {
        this.pictureCount = pictureCount;
    }


    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public DynamiclapsePoint getPoint() {
        return point;
    }

    public void setPoint(DynamiclapsePoint point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "DynamiclapseModel{" +
                "interval=" + interval +
                ", pictureCount=" + pictureCount +
                '}';
    }
}
