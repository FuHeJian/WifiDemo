package com.example.wifidemo1.wifi;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.PopUpToBuilder;

import com.example.wifidemo1.App;
import com.example.wifidemo1.Function.MyConsumer;
import com.example.wifidemo1.activity.RequestCode;
import com.example.wifidemo1.activity.base.BaseActivity;
import com.example.wifidemo1.bluetooth.BlueToothUtil;
import com.example.wifidemo1.broadcaster.BroadcasterUtil;
import com.example.wifidemo1.broadcaster.WiFiReceiverJava;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.network.NetCallBack.NetWorkCallbackAsync;
import com.example.wifidemo1.permission.PermissionUtil;

import org.jetbrains.annotations.Nullable;

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
import kotlin.jvm.internal.Intrinsics;
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
        if (!makeWifiOpened(context, name, bssid, listener)) return;//结果不明或者wifi未打开直接返回。
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

    public void findCellularNetWorkForInterNet(Context context, NetWorkCallbackAsync.AvailableNetworkListener listener) {

        //获取ConnectivityManager服务
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        List<Integer> capabilities = new ArrayList<>();
        capabilities.add(NetworkCapabilities.NET_CAPABILITY_INTERNET);

        List<Integer> transportTypes = new ArrayList<>();
        transportTypes.add(NetworkCapabilities.TRANSPORT_CELLULAR);

        ConnectivityManager.NetworkCallback callback = getDefaultCallback(listener, capabilities, transportTypes, connectivityManager);

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

    public boolean networkIsMatchCapabilitiesAndTransportList(ConnectivityManager connectivityManager, NetworkCapabilities allCapabilities, List<Integer> capabilities, List<Integer> tansPorts) {
        NetworkCapabilities networkCapabilities = allCapabilities;
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
            public boolean match(NetworkCapabilities capabilities1) {
                return networkIsMatchCapabilitiesAndTransportList(connectivityManager, capabilities1, capabilities, transPorts);
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

    /**
     * 确保wifi打开
     *
     * @param context 同于获取wifi服务
     */
    public static boolean makeWifiOpened(Context context, String name, MacAddress bssid, NetWorkCallbackAsync.AvailableNetworkListener listener) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            if (PermissionUtil.isHighAndroidQ()) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_WIFI_SETTINGS);
                if (context instanceof BaseActivity) {
                    ((BaseActivity) context).addRegisterForActivityResultListener(new BaseActivity.RegisterForActivityResultListener() {
                        @Override
                        public void onResult(ActivityResult result) {
                            if (result != null) {
                                if (wifiManager.isWifiEnabled()) {
                                    Toast.makeText(context, (CharSequence) "正在连接wifi", Toast.LENGTH_SHORT).show();
                                    if (name != null && bssid != null && listener != null) {
                                        INSTANCE.connectWifi(context, name, bssid, listener);
                                    }
                                } else {
                                    Toast.makeText(context, (CharSequence) "未打开wifi", Toast.LENGTH_SHORT).show();
                                }
                            }
                            ((BaseActivity) context).removeRegisterForActivityResultListener(this);
                        }
                    });
                    ActivityResultLauncher<Intent> launcher = ((BaseActivity) context).getRegisterForActivityResult();
                    if (launcher != null) {
                        launcher.launch(intent);
                    }
                } else {
                    context.startActivity(intent);
                }
            } else {
                return wifiManager.setWifiEnabled(true);
            }
        } else {
            return true;
        }
        return false;
    }


    /**
     * 主线程调用
     *
     * @param consumer
     */
    public void currentWifiName(MyConsumer<String> consumer) {
        if (!makeWifiOpened(App.GlobalManager.INSTANCE.getContext(null), null, null, null)) return;

        WifiManager wifiManager = (WifiManager) App.GlobalManager.INSTANCE.getContext(null).getSystemService(Context.WIFI_SERVICE);


        ConnectivityManager connectivityManager = (ConnectivityManager) App.GlobalManager.INSTANCE.getContext(null).getSystemService(Context.CONNECTIVITY_SERVICE);

        final NetworkRequest request =
                new NetworkRequest.Builder()
                        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                        .build();
        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                if (connectivityManager.getNetworkCapabilities(network).hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        WifiInfo wifiInfo = (WifiInfo) connectivityManager.getNetworkCapabilities(network).getTransportInfo();
                        consumer.accept(wifiInfo.getSSID());
                    }
                } else {
                    consumer.accept(null);
                }
                connectivityManager.unregisterNetworkCallback(this);
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                consumer.accept(null);
                connectivityManager.unregisterNetworkCallback(this);
            }

            @Override
            public void onLosing(@NonNull Network network, int maxMsToLive) {
                super.onLosing(network, maxMsToLive);
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
            }

            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
            }

            @Override
            public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties);
            }

            @Override
            public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
                super.onBlockedStatusChanged(network, blocked);
            }

        };
        MyLog.printLog("当前类:WifiUtil,当前方法：currentWifiName,当前线程:" + Thread.currentThread().getName() + ",信息:获取连接的wifi名字");
        connectivityManager.requestNetwork(request, callback);
    }
}
