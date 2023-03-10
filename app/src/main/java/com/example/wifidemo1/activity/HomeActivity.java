package com.example.wifidemo1.activity;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModel;


import com.example.wifidemo1.activity.base.BaseDataBindingActivity;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.activity.impl.HomeActivityInitViewImpl;
import com.example.wifidemo1.databinding.ActivityMainBinding;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.viewmodel.HomeViewModel;


/**
 * @author: fuhejian
 * @date: 2023/3/6
 */
public class HomeActivity extends BaseDataBindingActivity<ActivityMainBinding> {

    @SuppressLint("MissingPermission")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPermission();

        //wifi配置
        //registerWiFiReceiver()

    }

    @Override
    public <M extends ViewModel> Class<M> getViewModel() {
        return (Class<M>) HomeViewModel.class;
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

    @NonNull
    @Override
    protected ActivityResultLauncher<Intent> createRegisterForActivityResult() {
        //this::引用lambda的简化用法
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::dispatchRegisterForActivityResultListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
