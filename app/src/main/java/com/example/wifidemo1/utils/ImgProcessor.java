package com.example.wifidemo1.utils;

import android.graphics.Bitmap;
import android.view.Surface;

public class ImgProcessor {
    static{
        System.loadLibrary("imgprocessor");
    }
    public static native long init();
    public static native long setOutputWindow(long handle,Surface surface);
    public static native long setPeaking(long handle, int gsize,int lsize,int thres,boolean open);
    public static native long initImgSize(long handle, int width, int height);
    public static native long updateMjpegImg(long handle, byte[] data);
    public static native long updateYuv420pImg(long handle, byte[] yuv420p, int width, int height);
    public static native long getPeakingBitmap(long handle, byte[] yuv420p, Bitmap outBmp, int width, int height);
    public static native long release(long handle);
}
