package com.example.wifidemo1.utils;

import android.view.Surface;

public class MJpeg {

    static{
        System.loadLibrary("mjpeg-lib");
    }

    public static native long setOutputWindow(Surface surface);
    public static native long setPeaking(long handle, int gsize,int lsize,int thres,boolean fg);
    public static native long initSize(long handle,int width, int height);
    public static native long setMjpegImage(long handle, byte[] data);
    public static native long release(long handle);

}
