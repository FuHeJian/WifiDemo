package com.example.wifidemo1.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import com.example.wifidemo1.wifi.WifiUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.net.SocketFactory;

import okhttp3.OkHttpClient;
import retrofit2.http.PUT;

public class NetWorkUtil {

    public static NetWorkUtil INSTANCE;

    static {
        INSTANCE = new NetWorkUtil();
    }

    private OkHttpClient cacheOkHttpClient;

    private Network mPolarisLanNetWork = null;

    private Network mCellularNetWork = null;

    /**
     * 将当前进程的用于访问InterNet的默认网络
     */
    static public boolean setProcessNetWork(Context context, Network network) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetwork() != network) {
            return connectivityManager.bindProcessToNetwork(network);
        } else {
            return true;
        }
    }

    /**
     * URL转IP
     */
    static public InetAddress[] url2Ip(Context context, String url) throws UnknownHostException {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network netWork = connectivityManager.getActiveNetwork();
        if (netWork != null) {
            return netWork.getAllByName(url);
        } else {
            return null;
        }
    }

    /**
     * 将OKHttp绑定到指定网络，如果network为null,则返回上一次创建的OkHttpClient
     *
     * @return
     */
    public OkHttpClient getOkHttpClient(SocketFactory socketFactory) {
        if (cacheOkHttpClient == null || (socketFactory != null && socketFactory != cacheOkHttpClient.socketFactory())) {
            cacheOkHttpClient = new OkHttpClient.Builder()
                    .socketFactory(socketFactory)
                    .connectTimeout(java.time.Duration.ofSeconds(15))
                    .build();
        }
        return cacheOkHttpClient;
    }

    public void setNetWorkForNet(Network network){
        mCellularNetWork = network;
    }

    public Network getNetWorkForNet(){
        return mCellularNetWork;
    }

    public void setPolarisLanNetWork(Network polarisLanNetWork) {
        this.mPolarisLanNetWork = polarisLanNetWork;
    }

    public Network getPolarisLanNetWork(){
        return this.mPolarisLanNetWork;
    }

}
