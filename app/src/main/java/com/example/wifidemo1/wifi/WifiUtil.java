package com.example.wifidemo1.wifi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wifidemo1.broadcaster.BroadcasterUtil;
import com.example.wifidemo1.broadcaster.WiFiReceiverJava;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.network.NetCallBack.NetWorkCallbackAsync;
import com.example.wifidemo1.permission.PermissionUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InterfaceAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.observers.DisposableAutoReleaseMultiObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttp;

public class WifiUtil {

    public static WifiUtil INSTANCE;
    private CompositeDisposable mCompositeDisposable;

    static {
        INSTANCE = new WifiUtil();
    }

    /**
     * 连接指定wifi
     * listener中的方法是异步执行
     *
     * @param context
     * @param name
     * @param bssid
     * @param listener
     */
    public void connectWifi(Context context, String name, MacAddress bssid, NetWorkCallbackAsync.AvailableNetworkListener listener) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //获取ConnectivityManager服务
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            //需要密码再加上
            //.setWpa2Passphrase("dyqy2022")
            //配置需要连接的wifi信息
            WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder().setSsid(name).setBssid(bssid).build();
            NetworkRequest networkRequest = new NetworkRequest.Builder()
                    .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                    .setNetworkSpecifier(wifiNetworkSpecifier)
                    .build();

            //配置异步callback
            List<Integer> transPorts = new ArrayList<>();
            transPorts.add(NetworkCapabilities.TRANSPORT_WIFI);
            ConnectivityManager.NetworkCallback callback = getDefaultCallback(listener, null, transPorts, connectivityManager);

            //尝试使用指定的network向互联网访问
            //请求连接
            //如果没有找到networkRequest的要求的网络，还会自行去寻找合适的网络进行返回
            connectivityManager.requestNetwork(networkRequest, callback);

        } else {
            //低于安卓10
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = name;
            wifiConfiguration.BSSID = bssid.toString();
            int netId = wifiManager.addNetwork(wifiConfiguration);
            wifiManager.enableNetwork(netId, true);
        }
    }

    public void findNetWorkForInterNet(Context context, NetWorkCallbackAsync.AvailableNetworkListener listener) {

        //获取ConnectivityManager服务
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        List<Integer> capabilities = new ArrayList<>();
        capabilities.add(NetworkCapabilities.NET_CAPABILITY_INTERNET);

        ConnectivityManager.NetworkCallback callback = getDefaultCallback(listener, capabilities, null, connectivityManager);

        //请求获取匹配networkRequest的最佳网络
        //如果没有找到networkRequest的要求的网络，还会自行去寻找合适的网络进行返回,，如果获取到的网络必须满足某项需求
        //需要再onAvailable对network进行判断
        connectivityManager.requestNetwork(networkRequest, callback);

    }

    /**
     * NetWork 的 capability 是否与指定的Capability 是否匹配  只能匹配单个
     *
     * @param connectivityManager
     * @param netWork
     * @param capability
     * @return
     */
    public boolean networkIsMatch(ConnectivityManager connectivityManager, Network netWork, int capability) {
        return connectivityManager.getNetworkCapabilities(netWork).hasCapability(capability);
    }

    /**
     * NetWork 的 capability 是否与指定的Capability 是否匹配 匹配多个
     *
     * @param connectivityManager
     * @param netWork
     * @param capabilities
     * @return
     */
    public boolean networkIsMatchList(ConnectivityManager connectivityManager, Network netWork, List<Integer> capabilities) {
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(netWork);
        boolean result = true;
        for (int a : capabilities) {
            result = result && networkCapabilities.hasCapability(a);
        }
        return result;
    }

    public boolean networkIsMatchCapabilitiesAndTransportList(ConnectivityManager connectivityManager, Network netWork, List<Integer> capabilities, List<Integer> tansPorts) {
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(netWork);
        boolean result = true;

        if (capabilities != null) {
            for (int a : capabilities) {
                result = result && networkCapabilities.hasCapability(a);
            }
        }

        if (tansPorts != null && result) {
            for (int a : tansPorts) {
                result = result && networkCapabilities.hasTransport(a);
            }
        }

        return result;
    }

    /**
     * 获取默认的异步NetworkCallback实现
     *
     * @param listener
     * @param capabilities
     * @param connectivityManager
     * @return
     */
    private ConnectivityManager.NetworkCallback getDefaultCallback(NetWorkCallbackAsync.AvailableNetworkListener listener, List<Integer> capabilities, List<Integer> transPorts, ConnectivityManager connectivityManager) {
        ConnectivityManager.NetworkCallback callback = new NetWorkCallbackAsync(listener, new NetWorkCallbackAsync.Match() {
            @Override
            public boolean match(Network network) {
                return networkIsMatchCapabilitiesAndTransportList(connectivityManager, network, capabilities, transPorts);
            }
        });
        return callback;
    }

    public String intIp2Ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 注册WIFI广播
     */
    public static void registerWiFiReceiver(AppCompatActivity context) {
        WiFiReceiverJava receiver = new WiFiReceiverJava();
        IntentFilter flags = new IntentFilter();
        flags.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        flags.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        //activity销毁自动取消注册
        BroadcasterUtil.INSTANCE.registerWiFiBroadCastReceiverForActivity(context, receiver, flags);
    }

}
