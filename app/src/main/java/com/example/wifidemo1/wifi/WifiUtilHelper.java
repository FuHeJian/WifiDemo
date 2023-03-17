package com.example.wifidemo1.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import androidx.annotation.NonNull;

import com.example.wifidemo1.App;
import com.example.wifidemo1.Function.MyConsumer;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.network.NetCallBack.NetWorkCallbackAsync;
import com.example.wifidemo1.network.NetWorkUtil;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;

/**
 * @author: fuhejian
 * @date: 2023/3/13
 */
public class WifiUtilHelper {
    public static WifiUtilHelper INSTANCE;
    static {
        INSTANCE = new WifiUtilHelper();
    }

    public void updateNetWorkForNet(Context context){
        WifiUtil.INSTANCE.findCellularNetWorkForInterNet(context, new NetWorkCallbackAsync.AvailableNetworkListener() {
            @Override
            public void onSuccess(Network network, ConnectivityManager.NetworkCallback networkCallback) {
                MyLog.printLog("当前类:App,当前方法：onSuccess,信息:找到了可以上网的网络" + network);
                NetWorkUtil.INSTANCE.getOkHttpClient(network.getSocketFactory());
                NetWorkUtil.INSTANCE.setNetWorkForNet(network);
            }

            @Override
            public void onUpdate(Network network) {
                super.onUpdate(network);
                MyLog.printLog("当前类:App,当前方法：onSuccess,信息:找到了可以上网的网络" + network);
                NetWorkUtil.INSTANCE.getOkHttpClient(network.getSocketFactory());
                NetWorkUtil.INSTANCE.setNetWorkForNet(network);
            }
        });
    }

}
