package com.example.wifidemo1.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.UnknownHostException;

public class NetWorkUtil {

    static NetWorkUtil  INSTANCE;
    static {
        INSTANCE = new NetWorkUtil();
    }

    /**
     * 将当前进程的用于访问InterNet的默认网络
     */
    static public boolean setProcessNetWork(Context context, Network network)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getActiveNetwork()!=network){
            return connectivityManager.bindProcessToNetwork(network);
        }else {
            return true;
        }
    }

    /**
     * URL转IP
     */
    static public InetAddress[] url2Ip(Context context, String url) throws UnknownHostException {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network netWork  = connectivityManager.getActiveNetwork();
        if(netWork!=null)
        {
            return netWork.getAllByName(url);
        }
        else {
            return null;
        }
    }


}
