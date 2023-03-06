package com.example.wifidemo1.socket;

import android.app.WallpaperManager;
import android.content.Context;
import android.net.Network;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.oksocket.client.impl.client.PulseManager;
import com.example.wifidemo1.oksocket.client.sdk.*;
import com.example.wifidemo1.oksocket.client.sdk.client.ConnectionInfo;
import com.example.wifidemo1.oksocket.client.sdk.client.OkSocketFactory;
import com.example.wifidemo1.oksocket.client.sdk.client.OkSocketOptions;
import com.example.wifidemo1.oksocket.client.sdk.client.action.ISocketActionListener;
import com.example.wifidemo1.oksocket.client.sdk.client.connection.*;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IPulseSendable;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.ISendable;
import com.example.wifidemo1.oksocket.core.pojo.OriginalData;
import com.example.wifidemo1.socket.sendable.OrderDataSendable;
import com.example.wifidemo1.socket.sendable.PulseSendable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogRecord;

import javax.net.SocketFactory;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * @author: fuhejian
 * @date: 2023/3/2
 */
public class SocketUtil {
    public static SocketUtil INSTANCE;

    static {
        INSTANCE = new SocketUtil();
    }

    private OkHttpClient okHttpClientCache;

    Handler mMainHandler;

    /**
     * @param
     * @return
     * @description 创建socket 并 绑定到相应的NetWork, 将socket绑定到activity的生命周期，
     * @author fuhejian
     * @time 2023/3/2
     */
    public IConnectionManager connectSocket(String ip, int port, Network network, AppCompatActivity activity) {

        IConnectionManager socket = OkSocket.open(ip, port);

        //option配置
        OkSocketOptions options = socket.getOption();

        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(options);
        //设置断线重连的规则
//      builder.setReconnectionManager(defaultReconnectManager).build())

        //绑定到指定的wifi,就不用去调用bindProcessNetWork了。
        if(network!=null)
        {
            OkSocketFactory factory = SocketUtil.INSTANCE.socketFactoryToOkSocketFactoryForBindNetWork(network);
            builder.setSocketFactory(factory);
        }
        //创建socket，并连接地址
        socket.option(builder.build());

        //注册监听
        ISocketActionListener iSocketActionListener = new ISocketActionListener() {
            @Override
            public void onSocketIOThreadStart(String s) {
                MyLog.printLog("onSocketIOThreadStart");
            }

            @Override
            public void onSocketIOThreadShutdown(String s, Exception e) {
                MyLog.printLog("onSocketIOThreadShutdown");
            }

            @Override
            public void onSocketReadResponse(ConnectionInfo connectionInfo, String s, OriginalData originalData) {

                //心跳处理，防止自主断开
//                boolean isPulse = Arrays.equals(originalData.getBodyBytes(), RequestCommands.Pulse);
//                if (isPulse) {

                OkSocket.open(connectionInfo).getPulseManager().feed();
//                }
                String value = new String(originalData.getBodyBytes(), Charset.forName("utf-8"));

                Log.w(TAG, "onSocketReadResponse: value:" + value);

                Flowable.create(new FlowableOnSubscribe<Object>() {

                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull FlowableEmitter<Object> emitter) throws Throwable {

                    }
                }, BackpressureStrategy.BUFFER);

//                sendOrder(780, 2, null, socket);
            }

            @Override
            public void onSocketWriteResponse(ConnectionInfo connectionInfo, String s, ISendable iSendable) {

            }

            @Override
            public void onPulseSend(ConnectionInfo connectionInfo, IPulseSendable iPulseSendable) {
                //心跳发送后回调
                MyLog.printLog("onPulseSend");
            }

            @Override
            public void onSocketDisconnection(ConnectionInfo connectionInfo, String s, Exception e) {
                socket.unRegisterReceiver(this);
                MyLog.printLog("onSocketDisconnection");
            }

            @Override
            public void onSocketConnectionSuccess(ConnectionInfo connectionInfo, String s) {

                //由于PulseManager是connect后通过子线程设置的，如果connect后立即调用，getPulseManager可能返回空，但是连接成功是在PulseManager之后，所以这里获取一定不为空。
                OkSocket.open(connectionInfo).getPulseManager().setPulseSendable(new PulseSendable()).pulse();
/*
                //设置心跳包，不然无法触发心跳
                pulseManager.setPulseSendable(new PulseSendable());
                //开始心跳
                pulseManager.pulse();*/

                MyLog.printLog("onSocketConnectionSuccess");

            }

            @Override
            public void onSocketConnectionFailed(ConnectionInfo connectionInfo, String s, Exception e) {
                MyLog.printLog("onSocketConnectionFailed");
            }
        };

        socket.registerReceiver(iSocketActionListener);

        socket.connect();

        if (mMainHandler == null) {
            mMainHandler = new Handler(activity.getMainLooper());
        }

        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                activity.getLifecycle().addObserver(new DefaultLifecycleObserver() {
                    @Override
                    public void onDestroy(@NonNull LifecycleOwner owner) {
                        socket.unRegisterReceiver(iSocketActionListener);
                        socket.disconnect();
                        mMainHandler.removeCallbacksAndMessages(null);
                    }
                });
            }
        });

        AndroidSchedulers.mainThread().createWorker();

        return socket;

    }

    public WebSocket connectWebSocket(String address, Network network, @NonNull WebSocketListener listener) {

        if (okHttpClientCache == null) {

            OkHttpClient.Builder okBuilder = new OkHttpClient.Builder();
            okBuilder.pingInterval(20, TimeUnit.SECONDS);
            okBuilder.socketFactory(network.getSocketFactory());
            okBuilder.connectTimeout(10, TimeUnit.SECONDS);
            OkHttpClient okHttpClient = okBuilder.build();

            okHttpClientCache = okHttpClient;
        }

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url("ws://" + address + "/");

        Request request = requestBuilder.build();

        return okHttpClientCache.newWebSocket(request, listener);

    }

    private static final String TAG = "SocketUtil";

    //780, 2, null
    public synchronized boolean sendOrder(int code, int sptype, String msg, IConnectionManager socket) {

        int spkey = 1;
        String cmd = spkey + "&" + code + "&" + sptype + "&" + msg + "#";
        Log.d(TAG, "指令 sendOrder: cmd =" + cmd);
        try {
            socket.send(new OrderDataSendable(cmd));
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    public OkSocketFactory socketFactoryToOkSocketFactoryForBindNetWork(Network network) {
        return new OkSocketFactoryStub(network);
    }

}
