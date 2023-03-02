package com.example.wifidemo1.brocaster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.wifidemo1.log.MyLog;

import java.security.PrivilegedAction;
import java.util.List;

public class WiFiReceiverJava extends BroadcastReceiver {

    private WifiManager wifiManager;
    private static WiFiReceiverJava INSTANCE;
    private ConnectivityManager connectivityManager;
    private boolean CanRequestNetWork;

    private boolean isConnected;

    static {
        INSTANCE = new WiFiReceiverJava();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //初始化wifiManager
        if (wifiManager == null) {
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            wifiManager.startScan();
        }

        if (connectivityManager == null) {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        //获取数据
        if (intent != null && !CanRequestNetWork) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            CanRequestNetWork = context.checkSelfPermission("android.permission.CHANGE_NETWORK_STATE") == PackageManager.PERMISSION_GRANTED;
/*            if (success) {
                scanSuccess();
            } else {
//                scanFailure();
            }*/
            if(!isConnected)
                scanSuccess();
        }

    }

    private void scanSuccess() {
        //有wifi数据更新
        List<ScanResult> scanResultList = wifiManager.getScanResults();
        //打印日志
        log(scanResultList);
    }

    private void scanFailure() {
        //使用旧wifi结果处理
        List<ScanResult> scanResultList = wifiManager.getScanResults();

        log(scanResultList);
    }

    /**
     * 打印日志
     *
     * @param list
     */
    @SuppressLint("MissingPermission")
    private void log(List<ScanResult> list) {
        list.forEach(str -> {
//            MyLog.printLog(str.SSID);
            if ("QIUYI".equals(str.SSID)) {
                wifiManager.getConfiguredNetworks().forEach(configuration -> {
                    MyLog.printLog("扫描" + configuration.SSID);
                    if (configuration.SSID.equals("\"QIUYI\"") && CanRequestNetWork) {
                        MyLog.printLog("尝试连接" + configuration.SSID);
                        MacAddress MACADD = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                            MACADD = MacAddress.fromString(str.BSSID);
                            MyLog.printLog("这里"+str.BSSID);
                            connectWifi(configuration.networkId, str.SSID, MACADD);
                        }
                    }
                });
            }
        });
    }

    private void connectWifi(int netId, String ssid, MacAddress bssid) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            WifiNetworkSpecifier wifiNetworkSpecifier = new WifiNetworkSpecifier.Builder().setSsid(ssid).setBssid(bssid).setWpa2Passphrase("dyqy2022").build();
//.removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            NetworkRequest networkRequest = new NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).setNetworkSpecifier(wifiNetworkSpecifier).build();
            ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    isConnected = true;
                }

                @Override
                public void onLosing(@NonNull Network network, int maxMsToLive) {
                    super.onLosing(network, maxMsToLive);
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                }

            };
            connectivityManager.requestNetwork(networkRequest, callback);
        } else {
            wifiManager.enableNetwork(netId, true);
        }
    }
}