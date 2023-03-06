package com.example.wifidemo1.activity;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import androidx.annotation.RawRes;
import androidx.core.app.ActivityCompat;


import com.example.wifidemo1.R;
import com.example.wifidemo1.activity.base.BaseDataBindingActivity;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.activity.impl.HomeActivityInitViewImpl;
import com.example.wifidemo1.databinding.ActivityMainBinding;
import com.example.wifidemo1.log.MyLog;


/**
 * @author: fuhejian
 * @date: 2023/3/6
 */
public class HomeActivity extends BaseDataBindingActivity<ActivityMainBinding> {

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @SuppressLint("MissingPermission")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPermission();

        //wifi配置
        //registerWiFiReceiver()

    }

    @NonNull
    @Override
    public ActivityMainBinding createDataBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @NonNull
    @Override
    public InitView<ActivityMainBinding> createIntiView() {
        return new HomeActivityInitViewImpl();
    }

    /**
     * 获取权限
     */
    private void getPermission() {
        String[] permissions = {
                "android.permission.CHANGE_WIFI_STATE",
                "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_WIFI_STATE",
                "android.permission.CHANGE_NETWORK_STATE",
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_SCAN",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.BLUETOOTH_ADVERTISE",
                "android.permission.BLUETOOTH_CONNECT",
                "android.permission.ACCESS_FINE_LOCATION",//>=android.os.Build.VERSION_CODES.Q时需申请
                Manifest.permission.ACCESS_NETWORK_STATE};

        int requestCode = RequestCode.PERMISSIONS;
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    /**
     * 权限授权回调
     */
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            @NonNull int[] grantResults
    ) {
        for (int i = 0; i < permissions.length; i++) {
            MyLog.printLog(permissions[i] + "授权结果" + (grantResults[i] == PackageManager.PERMISSION_GRANTED));
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
