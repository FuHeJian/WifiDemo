package com.example.wifidemo1.model;

import android.graphics.Bitmap;

import com.example.wifidemo1.utils.CMD;

public class BitmapStringModel {

    private Bitmap bitmap;
    private String name;

    private Bitmap adjustBitmap;
    private Bitmap defaultBitmap;
    private boolean showBitmap;
    private int fileType;

   public BitmapStringModel(Bitmap bitmap, String name) {
        this.bitmap = bitmap;
        this.name = name;
        showBitmap = true;
        fileType = CMD.FILE_TYPE_NORMAL;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getAdjustBitmap() {
        return adjustBitmap;
    }

    public void setAdjustBitmap(Bitmap adjustBitmap) {
        this.adjustBitmap = adjustBitmap;
    }

    public Bitmap getDefaultBitmap() {
        return defaultBitmap;
    }

    public void setDefaultBitmap(Bitmap defaultBitmap) {
        this.defaultBitmap = defaultBitmap;
    }

    public boolean isShowBitmap() {
        return showBitmap;
    }

    public void setShowBitmap(boolean showBitmap) {
        this.showBitmap = showBitmap;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }


}
