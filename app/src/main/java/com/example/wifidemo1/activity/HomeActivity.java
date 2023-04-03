package com.example.wifidemo1.activity;

import android.Manifest;
import android.annotation.SuppressLint;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModel;


import com.example.wifidemo1.App;
import com.example.wifidemo1.activity.base.BaseDataBindingActivity;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.activity.impl.HomeActivityInitViewImpl;
import com.example.wifidemo1.activity.theme.ThemeUtil;
import com.example.wifidemo1.databinding.ActivityMainBinding;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.network.PolarisSettings;
import com.example.wifidemo1.viewmodel.HomeViewModel;
import com.tencent.mmkv.MMKV;

import tv.danmaku.ijk.media.example.activities.VideoActivity;


/**
 * @author: fuhejian
 * @date: 2023/3/6
 */
public class HomeActivity extends BaseDataBindingActivity<ActivityMainBinding> {

    @SuppressLint("MissingPermission")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.INSTANCE.setSystemStatusBar(this,true,true);
        App.GlobalManager.INSTANCE.activitys.put(this,"HomeActivity");

        getPermission();
        initMMKV();

        //TODO
        Intent intent = new Intent();
        intent.setComponent( new ComponentName(this, VideoActivity.class));
        startActivity(intent);

        VideoActivity.intentTo(this,"http://v26-web.douyinvod.com/f6fa7896d68d8c413847506bf14074f8/6426ca9c/video/tos/cn/tos-cn-ve-15c001-alinc2/ocIJZyBAtBLB5gexEsUAofhAdcz7DDQpJtiqPD/?a=6383&ch=26&cr=3&dr=0&lr=all&cd=0%7C0%7C0%7C3&cv=1&br=1296&bt=1296&cs=0&ds=4&ft=bvTKJbQQqUYqfJEZao0OW_EklpPiX7vI7MVJEjDpwrbPD-I&mime_type=video_mp4&qs=0&rc=NDw4NGg3OjhmaGk1OjVoN0BpMzZqdjc6Zm9wajMzNGkzM0AvNmFgXzAvNTIxMDQ1MDY1YSNlL2ExcjRfcWBgLS1kLTBzcw%3D%3D&l=20230331185709467B35511C7FF905D206&btag=8000&testst=1680260234862","test");

        //wifi配置
        //registerWiFiReceiver()
    }

    public void initMMKV(){
        MMKV.initialize(this);
        MMKV mmkv = MMKV.defaultMMKV();
        SharedPreferences downloadedFileNamePath = getSharedPreferences("PolarisSettings", Context.MODE_PRIVATE);
        mmkv.importFromSharedPreferences(downloadedFileNamePath);
        downloadedFileNamePath.edit().clear().commit();//同步提交
        PolarisSettings.DownloadedFileNamePath = mmkv.decodeString("DownloadedFileNamePath");
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

    @Override
    protected ActivityResultLauncher<String[]> createRegisterForPermissionsResult() {
        return registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this::dispatchRegisterForPermissionsResultListener);
    }
    /**
     * 获取权限
     */
    private void getPermission() {
        String[] permissions = {
                "android.permission.CHANGE_WIFI_STATE",
                "android.permission.ACCESS_WIFI_STATE",
                "android.permission.CHANGE_NETWORK_STATE",
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_SCAN",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.BLUETOOTH_ADVERTISE",
                "android.permission.BLUETOOTH_CONNECT",
                "android.permission.ACCESS_FINE_LOCATION",//>=android.os.Build.VERSION_CODES.Q时需申请
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

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
}
