package com.example.wifidemo1.opengl;

/**
 * Created by panbin on 2018/3/8.
 */

public class ScaleModel {
    private int mOffset;
    private float x;
    private float y;
    private float z;

    public ScaleModel(int offset, float x, float y, float z) {
        this.mOffset = offset;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getmOffset() {
        return mOffset;
    }

    public void setmOffset(int mOffset) {
        this.mOffset = mOffset;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }
}
