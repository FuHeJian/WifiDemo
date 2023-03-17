package com.example.wifidemo1.risesetcalculate;

public class JniLoad {
    static {
        System.loadLibrary("native-lib");
    }


    public native long init(String latitude, String longitude, String elevation, String press, String tmp, String myjd, int daty);

    public native RiseSetInfo getRiseSet(long handle, String edbline);

    public native void calPlantRaDec(long handle, int plCode);

    public native float calPlantRa(long handle);

    public native float calPlantDec(long handle);



    public native void release(long handle);


}
