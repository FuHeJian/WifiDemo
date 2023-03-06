package com.example.wifidemo1.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

/**
 * by fuhejian
 * 2023-3-1
 */
public class PermissionUtil {

    /**
     * 检查Manifest.permission.BLUETOOTH_CONNECT权限
     */
    static public boolean checkBlueToothCONNECT(Context context) {
        if (!isHighAndroidS()) return true;
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查Manifest.permission.ACCESS_NETWORK_STATE权限
     */
    static public boolean checkCanGetNetState(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 是否高于androidS(api 31)
     */
    static public boolean isHighAndroidS() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S;
    }

    /**
     * 是否高于androidTIRAMISU(api 33)
     */
    static public boolean isHighAndroid33() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    /**
     * 是否高于androidTIRAMISU(api 33)
     * 和{@link #isHighAndroid33}一样
     */
    static public boolean isHighAndroidTIRAMISU() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    /**
     * 是否高于androidS(api 31)
     * 同{@link #isHighAndroidS}
     */
    static public boolean isHighAndroid31() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S;
    }

}
