package com.example.wifidemo1.socket;

import android.net.Network;

import androidx.annotation.NonNull;

import com.example.wifidemo1.oksocket.client.sdk.client.ConnectionInfo;
import com.example.wifidemo1.oksocket.client.sdk.client.OkSocketFactory;
import com.example.wifidemo1.oksocket.client.sdk.client.OkSocketOptions;

import java.lang.ref.WeakReference;
import java.net.Socket;
import java.nio.file.Watchable;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.SocketFactory;

/**
 * 用于OkSocket绑定network
 * @author: fuhejian
 * @date: 2023/3/2
 */
public class OkSocketFactoryStub extends OkSocketFactory {

    private WeakReference<Network> mNetWork;

    public OkSocketFactoryStub(Network network) {
        mNetWork = new WeakReference<>(network);
    }

    @Override
    public Socket createSocket(ConnectionInfo connectionInfo, OkSocketOptions okSocketOptions) throws Exception {
        //不要在这里调用socket的connect，因为Socket返回后，OkSocket还会再调用一次，这样会导致OkSocket启动失败。
        Socket socket;
        socket = new Socket();
        Network network = mNetWork.get();
        if (network != null) {
            network.bindSocket(socket);
        }
        return socket;
    }
}
