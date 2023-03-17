package com.example.wifidemo1.socket;

import com.example.wifidemo1.oksocket.client.sdk.OkSocket;
import com.example.wifidemo1.oksocket.client.sdk.client.ConnectionInfo;
import com.example.wifidemo1.oksocket.client.sdk.client.connection.AbsReconnectionManager;
import com.example.wifidemo1.oksocket.client.sdk.client.connection.IConnectionManager;

/**
 * @author: fuhejian
 * @date: 2023/3/14
 */
public class OkSocketReconnectManager extends AbsReconnectionManager {
    @Override
    public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
        IConnectionManager currentSocket = SocketUtil.INSTANCE.CurrentSocket;
    }

    @Override
    public void onSocketConnectionSuccess(ConnectionInfo info, String action) {

    }

    @Override
    public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {

    }

}
