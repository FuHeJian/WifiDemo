package com.example.wifidemo1.fragment.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.example.wifidemo1.customview.TrochalDiskBackground;
import com.example.wifidemo1.databinding.CameraBinding;
import com.example.wifidemo1.fragment.CameraFragment;
import com.example.wifidemo1.fragment.i.FragmentInitView;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.model.FirmWareVersionInfo;
import com.example.wifidemo1.model.PolarisVersion;
import com.example.wifidemo1.network.NetWorkUtil;
import com.example.wifidemo1.network.PolarisSettings;
import com.example.wifidemo1.network.retrofit.PolarisNetWork;
import com.example.wifidemo1.oksocket.common.interfaces.utils.TextUtils;
import com.example.wifidemo1.socket.SocketUtil;
import com.example.wifidemo1.utils.OrderCommunication;
import com.example.wifidemo1.utils.UpdateVersionUtil;
import com.example.wifidemo1.wifi.WifiUtilHelper;
import com.google.android.material.math.MathUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * @author: fuhejian
 * @date: 2023/3/9
 */
public class CameraFragmentImpl implements FragmentInitView<CameraBinding> {
    @Override
    public void initView(CameraBinding binding, LifecycleOwner lifecycleOwner) {
        binding.trochalDisk.setDragListener(new TrochalDiskBackground.DragListener() {
            private boolean b = false;

            @Override
            public void onDrag(int orientation, int d) {
                switch (orientation) {
                    case 0: {
                        if (d > 2000) d = 2000;
                        OrderCommunication.getInstance().SP_GIMBAL_HADJ_SPEED(String.valueOf(d));
                        break;
                    }
                    case 1: {
                        if (d > 2000) d = 2000;
                        OrderCommunication.getInstance().SP_GIMBAL_VADJ_SPEED(String.valueOf(d));
                        break;
                    }
                }
            }

            @Override
            public void onRelease() {

            }
        });
        test(binding);
    }

    public void test(CameraBinding binding) {

        binding.getVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetWorkUtil.INSTANCE.getNetWorkForNet() == null) {
                    WifiUtilHelper.INSTANCE.updateNetWorkForNet(v.getContext());
                    Toast.makeText(v.getContext(), "没有找到可以上网的网络", Toast.LENGTH_SHORT).show();
                }

                try {
                    if (binding.loading.getVisibility() != View.GONE) {
                        Toast.makeText(v.getContext(), "已经在下载了！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    PolarisNetWork.INSTANCE.getVersion(new Consumer<PolarisVersion>() {
                        @Override
                        public void accept(PolarisVersion polarisVersion) throws Throwable {

                            PolarisNetWork.INSTANCE.downloadRom(polarisVersion.firmWareVersionInfo.getRomUrl(), v.getContext(), polarisVersion.firmWareVersionInfo.getRomFileName(), new PolarisNetWork.DownLoadListener() {
                                @Override
                                public void onStart() {
                                    MyLog.printLog("当前类:CameraFragmentImpl,信息:" + "开始下载");
                                }

                                @Override
                                public void onDownLoad(int loadProcess) {
                                    binding.loading.show();
                                    binding.loading.setProgress(loadProcess);
                                    MyLog.printLog("当前类:CameraFragmentImpl,信息:" + "正在下载" + loadProcess);
                                }

                                @Override
                                public void onComplete(String md5, File file) {
                                    binding.loading.hide();
                                    if (polarisVersion.firmWareVersionInfo.getRomMd5() != null && polarisVersion.firmWareVersionInfo.getRomMd5().equals(md5)) {
                                        //校验成功
                                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                            @Override
                                            public void run() {
                                                MyLog.printLog("当前类:CameraFragmentImpl,信息:" + "下载完成");
                                                PolarisSettings.DownloadedFileNamePath = file.getPath();
                                                SharedPreferences downloadedFileNamePath = v.getContext().getSharedPreferences("PolarisSettings", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor edit = downloadedFileNamePath.edit();
                                                edit.putString("DownloadedFileNamePath", file.getPath());
                                                edit.apply();

                                                Toast.makeText(v.getContext(), "下载完成", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        //校验失败
                                        AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(v.getContext(), "文件校验失败", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    MyLog.printLog("当前类:CameraFragmentImpl,信息:" + "发生错误" + e.getMessage());
                                }
                            });
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(binding.getRoot().getContext(), "服务器获取数据失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.startUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送升级消息
                OrderCommunication.getInstance().SP_SET_UPGRADE_START();
            }
        });

        binding.checkUpgradeState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderCommunication.getInstance().SP_GET_DEVICE_VERSION();
            }
        });

    }

}
