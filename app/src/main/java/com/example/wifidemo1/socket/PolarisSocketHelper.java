package com.example.wifidemo1.socket;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.example.wifidemo1.App;
import com.example.wifidemo1.customview.MyTimer;
import com.example.wifidemo1.fragment.impl.HomeFragmentInitViewImpl;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.model.PolarisVersion;
import com.example.wifidemo1.network.NetWorkUtil;
import com.example.wifidemo1.network.PolarisSettings;
import com.example.wifidemo1.network.retrofit.PolarisNetWork;
import com.example.wifidemo1.oksocket.client.sdk.OkSocket;
import com.example.wifidemo1.oksocket.client.sdk.client.ConnectionInfo;
import com.example.wifidemo1.oksocket.common.interfaces.utils.TextUtils;
import com.example.wifidemo1.oksocket.core.pojo.OriginalData;
import com.example.wifidemo1.utils.CMD;
import com.example.wifidemo1.utils.FileUtil;
import com.example.wifidemo1.utils.OrderCommunication;
import com.example.wifidemo1.utils.UpdateVersionUtil;
import com.example.wifidemo1.utils.UpgradeUtils;
import com.example.wifidemo1.utils.UrlUtils;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.util.Arrays;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * @author: fuhejian
 * @date: 2023/3/13
 */
public class PolarisSocketHelper {

    public static PolarisSocketHelper INSTANCE;

    static {
        INSTANCE = new PolarisSocketHelper();
    }

    private byte[] oldData;

    private ResponseCode oldResponseCode;

    public void dispatchCode(ConnectionInfo connectionInfo, String s, OriginalData originalData) {
        if (SocketUtil.INSTANCE.CurrentSocket == null) return;
        if (Arrays.equals(RequestCommands.Pulse, originalData.getBodyBytes())) {
            //心跳处理
            if (OkSocket.open(connectionInfo).getPulseManager() == null) {
            } else {
                OkSocket.open(connectionInfo).getPulseManager().feed();
            }
            return;
        }
        if (oldData == null || !Arrays.equals(oldData, originalData.getBodyBytes())) {
            oldData = originalData.getBodyBytes();
            ResponseCode responseCode = ResponseCode.parseToResponseCode(new String(originalData.getBodyBytes()));
            oldResponseCode = responseCode;
        }
        if (oldResponseCode == null) return;
        if (oldResponseCode.retCode != null || oldResponseCode.hwCode != null || oldResponseCode.swCode != null) {
            parseA2MCode(connectionInfo, s, originalData, oldResponseCode);
        } else {
            parseM2ACode(connectionInfo, s, originalData, oldResponseCode);
        }
    }

    public void parseA2MCode(ConnectionInfo connectionInfo, String s, OriginalData originalData, ResponseCode responseCode) {
        switch (SocketUtil.INSTANCE.mCurrentState) {//用responseCode.order也可以,
            case CMD.SP_SET_UPGRADE_START: {//开始上传升级包
                MyLog.printLog("当前类:PolarisSocketHelper,当前方法：parseA2MCode,信息:升级响应消息" + responseCode);
                int value;
                if (responseCode.retCode != null) {
                    try {
                        value = Integer.parseInt(responseCode.retCode.value);
                        switch (value) {
                            case -1001: {
                                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(App.GlobalManager.INSTANCE.getContext(null), "远程设备没有插存储卡", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                break;
                            }
                            default:{
                                Message message = Message.obtain(HomeFragmentInitViewImpl.HomeFragmentHandler.INSTANCE);
                                MyLog.printLog("当前类:PolarisSocketHelper,当前方法：parseA2MCode,信息:开始上传");
                                if (!TextUtils.isEmpty(PolarisSettings.LatestSoftWarePolarisRomVersion) && UpdateVersionUtil.checkAppUpdate(PolarisSettings.SoftWarePolarisRomVersion, PolarisSettings.LatestSoftWarePolarisRomVersion) != 0 && PolarisSettings.DownloadedFileNamePath != null && !PolarisSettings.uploading) {
                                    if (PolarisSettings.SoftWarePolarisRomMD5 != null && PolarisSettings.SoftWarePolarisRomMD5.equals(FileUtil.getFileMd5(new File(PolarisSettings.DownloadedFileNamePath)))) {
                                        message.what = HomeFragmentInitViewImpl.HomeFragmentHandler.WHAT_UPGRADE_START;
                                        message.arg1 = 0;
                                        HomeFragmentInitViewImpl.HomeFragmentHandler.INSTANCE.sendMessage(message);
                                    } else {
                                        message.what = HomeFragmentInitViewImpl.HomeFragmentHandler.WHAT_UPGRADE_START;
                                        message.arg1 = 1;
                                        //去下载
                                        HomeFragmentInitViewImpl.HomeFragmentHandler.INSTANCE.sendMessage(message);
                                    }
                                } else {
                                    OrderCommunication.getInstance().SP_GET_DEVICE_VERSION();
                                }
                                break;
                            }

                        }
                    } catch (Exception e) {
                        //value没有解析成功
                        MyLog.printLog("当前类:PolarisSocketHelper,当前方法：parseA2MCode,当前线程:"+ Thread.currentThread().getName()+",信息:" + e.getMessage());
                    }
                }

                break;
            }
            case CMD.SP_LOAD_UPGRADE_FW_STATE: {//升级包上传状态

                break;
            }
            case CMD.SP_GET_DEVICE_VERSION: {//软硬件版本信息
                MMKV mmkv = MMKV.defaultMMKV();
                try {
                    mmkv.encode(PolarisSettings.HardWarePolarisRomVersion, responseCode.hwCode.value);
                    mmkv.encode(PolarisSettings.SoftWarePolarisRomVersion, responseCode.swCode.value);
                } catch (Exception e) {

                }

                //检查新版本
                try {
                    PolarisNetWork.INSTANCE.getVersion(new Consumer<PolarisVersion>() {
                        @Override
                        public void accept(PolarisVersion polarisVersion) throws Throwable {
                            //更新版本信息到
                            if (polarisVersion.firmWareVersionInfo.getSoftwareVersion() != null && responseCode.swCode.value != null) {
                                //TODO
                                PolarisSettings.SoftWarePolarisRomVersion = responseCode.swCode.value;
                                PolarisSettings.SoftWarePolarisRomMD5 = polarisVersion.firmWareVersionInfo.getRomMd5();
                                PolarisSettings.DownloadedFileNamePath = App.GlobalManager.INSTANCE.getContext(null).getExternalFilesDir(UrlUtils.firmware).getAbsolutePath() + "/" + polarisVersion.firmWareVersionInfo.getRomFileName();
                                PolarisSettings.RomFileName = polarisVersion.firmWareVersionInfo.getRomFileName();
                                PolarisSettings.SoftWarePolarisRomUrl = polarisVersion.firmWareVersionInfo.getRomUrl();
                                MyLog.printLog("当前类:PolarisSocketHelper,当前方法：accept,信息:当前版本" + responseCode.swCode.value);
                                PolarisSettings.LatestSoftWarePolarisRomVersion = polarisVersion.firmWareVersionInfo.getSoftwareVersion();
                                Message message = Message.obtain(HomeFragmentInitViewImpl.HomeFragmentHandler.INSTANCE);
                                message.what = HomeFragmentInitViewImpl.HomeFragmentHandler.WHAT_UPGRADE_DIALOG;
                                HomeFragmentInitViewImpl.HomeFragmentHandler.INSTANCE.sendMessage(message);
                            }
                        }
                    });
                } catch (Exception e) {

                }
                break;
            }
            case CMD.SP_SOCKET_CLIENT_TYPE:{//连接成功后需要发送这个消息,防止远程设备休眠
                //

                break;
            }
        }
    }

    private void parseM2ACode(ConnectionInfo connectionInfo, String s, OriginalData originalData, ResponseCode responseCode) {
        switch (responseCode.order) {
            case CMD.SP_PUSH_UPGRADE_STATUS: {
                if (responseCode.stateCode != null) {
                    try {
                        int responseValue = Integer.parseInt(responseCode.stateCode.value);
                        switch (responseValue) {
                            case 0: {
                                MyLog.printLog("当前类:PolarisSocketHelper,当前方法：parseM2ACode,信息:升级成功" + responseValue);
                                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(App.GlobalManager.INSTANCE.getContext("HomeActivity"), "升级成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                PolarisSettings.RomUpgradeIsSuccess = true;

                                break;
                            }
                            case -1: {
                                PolarisSettings.RomUpgradeIsSuccess = false;
                                AndroidSchedulers.mainThread().scheduleDirect(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(App.GlobalManager.INSTANCE.getContext("HomeActivity"), "升级失败", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                MyLog.printLog("当前类:PolarisSocketHelper,当前方法：parseM2ACode,信息:升级失败");
                                break;
                            }
                        }
                        OrderCommunication.getInstance().SP_PUSH_UPGRADE_STATUS(responseValue);
                    } catch (Exception e) {
                        //responseCode.stateCode.value不是数字
                        MyLog.printLog("当前类:PolarisSocketHelper,当前方法：parseM2ACode,当前线程:" + Thread.currentThread().getName() + ",信息:" + e.getMessage());
                    }
                }
                break;
            }
            case CMD.SP_GET_BAT_STATE | CMD.SP_PUSH_BAT_STATE: {//电量信息

                break;
            }

            case CMD.SP_PUSH_SD_HINT_ID: {//SD卡
                try {
                    int responseValue = Integer.parseInt(responseCode.sdHintCode.value);
                    MyLog.printLog("当前类:PolarisSocketHelper,当前方法：parseM2ACode,当前线程:" + Thread.currentThread().getName() + ",信息:SD卡" + responseValue);

                    switch (responseValue) {
                        case 1: {//没有sd卡

                            break;
                        }
                        case 3: {//sd卡可以使用

                            break;
                        }
                        case 4: {//sd卡满

                            break;
                        }
                        default: {//sd卡不可用

                        }
                    }
                } catch (Exception e) {

                }
                break;
            }
        }
    }

}
