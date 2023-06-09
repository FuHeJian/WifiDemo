package com.example.wifidemo1.log;

import android.util.Log;

public class MyLog {

    static final String TAG = "日志";

    public static void printLog(String str)
    {
        Log.i(TAG,str);
    }

    public static void printError(String str){
        Log.e(TAG,str);
    }

}
