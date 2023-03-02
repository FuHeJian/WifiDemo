package com.example.wifidemo1.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class PermissionUtil {

    static public boolean checkBlueToothCONNECT(Context context){
        if(!isHighAndroidS()) return true;
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    static public boolean checkCanGetNetState(Context context){
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    static public boolean isHighAndroidS(){
        return android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.S;
    }

    static public boolean isHighAndroid33(){
        return android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU;
    }

    static public boolean isHighAndroidTIRAMISU(){
        return android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU;
    }

    static public boolean isHighAndroid31(){
        return android.os.Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.S;
    }

}
