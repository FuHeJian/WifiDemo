package com.example.wifidemo1.socket;

import android.app.WallpaperManager;
import android.content.Context;
import android.net.Network;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
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
     * @param listener socket监听
     * @return
     * @description 创建socket 并 绑定到相应的NetWork, 将socket绑定到activity的生命周期，
     * @author fuhejian
     * @time 2023/3/2
     */
    public IConnectionManager connectSocket(String ip, int port, Network network, AppCompatActivity activity, IConnectionManager oldSocket, ISocketActionListener listener) {

        //对旧scoket进行销毁
        if(oldSocket!=null&&oldSocket.getRemoteConnectionInfo()!=null&&OkSocket.hasCache(oldSocket.getRemoteConnectionInfo()))
        {
            OkSocket.open(oldSocket.getRemoteConnectionInfo()).disconnect();//断开旧socket连接,内部会自动移除listener
            OkSocket.removeCache(oldSocket.getRemoteConnectionInfo());//移除缓存，保证open(connection)获取到最新的缓存值
        }
        IConnectionManager socket = OkSocket.open(ip, port);

        //option配置
        OkSocketOptions options = socket.getOption();

        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(options);
        //设置断线重连的规则
        //builder.setReconnectionManager(defaultReconnectManager).build())

        //绑定到指定的wifi,就不用去调用bindProcessNetWork了。
        if(network!=null)
        {
            OkSocketFactory factory = SocketUtil.INSTANCE.socketFactoryToOkSocketFactoryForBindNetWork(network);
            builder.setSocketFactory(factory);
        }
        //创建socket，并连接地址
        socket.option(builder.build());

        //注册监听
        ISocketActionListener iSocketActionListener = listener;
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
