package com.example.wifidemo1.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityManagerCompat;

import com.example.wifidemo1.bluetooth.BlueToothUtil;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.permission.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class BlueToothScanHelper {

    public static void scanBLE(Context context) {

        List<ScanFilter> scanFilterList = new ArrayList();

        ScanFilter scanFilter = new ScanFilter.Builder()
                .build();
        scanFilterList.add(scanFilter);
        ScanSettings.Builder scanSettings = new ScanSettings.Builder();

        if (isForeground(context)) {
            scanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        }

        ScanCallback scanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if (!PermissionUtil.checkBlueToothCONNECT(context)) {

                    return;
                }
               @SuppressLint("MissingPermission") String str  =  result.getDevice().getName();

                MyLog.printLog(str!=null?str:result.getDevice().getAddress());
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                MyLog.printLog("扫描失败"+errorCode);
            }
        };

        BlueToothUtil.INSTANCE.scanBLE(context, scanFilterList, scanSettings.build(), scanCallback, 20000);

    }

    private static boolean isForeground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo process : runningAppProcesses) {
            if (context.getPackageName().equals(process.processName)) {
                MyLog.printLog("是否在前台运行"+(process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND));
                return process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
            }
        }
        return false;
    }

}
