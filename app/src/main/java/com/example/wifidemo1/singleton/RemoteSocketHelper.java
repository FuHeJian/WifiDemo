package com.example.wifidemo1.singleton;

import android.os.Handler;
import android.util.Log;

import com.example.wifidemo1.oksocket.client.impl.client.PulseManager;
import com.example.wifidemo1.oksocket.client.impl.client.action.ActionDispatcher;
import com.example.wifidemo1.oksocket.client.sdk.OkSocket;
import com.example.wifidemo1.oksocket.client.sdk.client.ConnectionInfo;
import com.example.wifidemo1.oksocket.client.sdk.client.OkSocketOptions;
import com.example.wifidemo1.oksocket.client.sdk.client.action.SocketActionAdapter;
import com.example.wifidemo1.oksocket.client.sdk.client.connection.DefaultReconnectManager;
import com.example.wifidemo1.oksocket.client.sdk.client.connection.IConnectionManager;
import com.example.wifidemo1.oksocket.client.sdk.client.connection.NoneReconnect;
import com.example.wifidemo1.oksocket.common.interfaces.utils.TextUtils;
import com.example.wifidemo1.oksocket.core.iocore.MessageSendData;
import com.example.wifidemo1.oksocket.core.iocore.PulseSendData;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IPulseSendable;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.ISendable;
import com.example.wifidemo1.oksocket.core.pojo.OriginalData;
import com.example.wifidemo1.oksocket.core.utils.SLog;

import java.nio.charset.Charset;


/**
 * Created by LQF on 2019/12/17.
 */

public class RemoteSocketHelper {
    private static final String TAG = "SocketHelper";

    private ConnectionInfo connectionInfo;
    private IConnectionManager iConnectionManager;
    private PulseManager pulseManager;
    private DefaultReconnectManager defaultReconnectManager;
    private OkSocketOptions okSocketOptions;

    private String[] parseString;
    private String[] messageString;
    private Handler mHandler;
    private PulseSendData pulseSendData;
    private StringBuilder resultBuilder;


    public RemoteSocketHelper(RemoteSocketHelperListener listener) {
        pulseSendData = new PulseSendData();
        resultBuilder = new StringBuilder();
        this.listener = listener;
    }


    private SocketActionAdapter socketActionAdapter = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            Log.d(TAG, "onSocketConnectionSuccess --> 连接成功(Connecting Success) action = " + action);

            if (listener != null)
                listener.onSocketConnectionSuccess();


            if (pulseManager == null) {
                pulseManager = OkSocket.open(connectionInfo).getPulseManager();
            }
            pulseManager.setPulseSendable(pulseSendData);
            pulseManager.pulse();

            iConnectionManager.option(
                    new OkSocketOptions.Builder(iConnectionManager.getOption())
                            .setReconnectionManager(defaultReconnectManager).build());

        }

        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                Log.d(TAG, "onSocketDisconnection --> 异常断开(Disconnected with exception):" + e.getMessage());
            } else {
                Log.d(TAG, "onSocketDisconnection --> 正常断开(Disconnect Manually)");
            }

            if (listener != null)
                listener.onSocketDisconnection();


        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            Log.d(TAG, "onSocketConnectionFailed --> 连接失败(Connecting Failed) e =" + e);

            if (listener != null)
                listener.onSocketConnectionFailed();
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, final OriginalData data) {
            socketReadResponse(data);
        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {

        }

        @Override
        public void onPulseSend(ConnectionInfo info, IPulseSendable data) {

        }
    };

    private void initManager(String socketIP, int socketPORT) {
        connectionInfo = new ConnectionInfo(socketIP, socketPORT);
        okSocketOptions = new OkSocketOptions.Builder()
                .setReconnectionManager(new NoneReconnect())
                .setConnectionHolden(true)
                .setConnectTimeoutSecond(1)
                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                    @Override
                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                        if (mHandler != null) {
                            mHandler.post(runnable);
                        }
                    }
                })
                .build();
        OkSocketOptions.setIsDebug(true);// TODO: 2020/1/4
        SLog.setIsDebug(true);// TODO: 2020/1/8
        iConnectionManager = OkSocket.open(connectionInfo).option(okSocketOptions);
        iConnectionManager.registerReceiver(socketActionAdapter);
        pulseManager = OkSocket.open(connectionInfo).getPulseManager();

        defaultReconnectManager = new DefaultReconnectManager();
        defaultReconnectManager.attach(iConnectionManager);
    }

    public boolean sockerIsConnect() {
        if (iConnectionManager == null)
            return false;

        return iConnectionManager.isConnect();
    }


    public void connectSocket(String socketIP, int socketPORT) throws NullPointerException {
        Log.d(TAG, "connectSocket: --- ");
        mHandler = new Handler();
        if (iConnectionManager == null) {
            initManager(socketIP, socketPORT);
        }
        if (!iConnectionManager.isConnect()) {
            initManager(socketIP, socketPORT);
            iConnectionManager.connect();
        }
    }


    public void disConnectSocket() {
        Log.d(TAG, "disConnectSocket: --- ");
        if (iConnectionManager == null) {
            return;
        }
        iConnectionManager.option(
                new OkSocketOptions.Builder(iConnectionManager.getOption())
                        .setConnectionHolden(false).build());
        if (pulseManager != null) {
            pulseManager.dead();
            pulseManager = null;
        }
        if (defaultReconnectManager != null) {
            defaultReconnectManager.detach();
        }
        if (iConnectionManager.isConnect()) {
            iConnectionManager.disconnect();
        }
        iConnectionManager.unRegisterReceiver(socketActionAdapter);
    }

    public void sendMessage(String msg) throws Exception {
        if (iConnectionManager == null || TextUtils.isEmpty(msg)) {
            throw new NullPointerException("mManager = null || msg = " + msg);
        } else if (!iConnectionManager.isConnect()) {
            throw new IllegalStateException("Unconnected");
        }
        iConnectionManager.send(new MessageSendData(msg));
    }

    private void socketReadResponse(OriginalData data) {
        if (data == null) {
            Log.e(TAG, "socketReadResponse: data = null");
            return;
        }
        String value = new String(data.getBodyBytes(), Charset.forName("utf-8"));

        resultBuilder.append(value);
        if (!value.endsWith("#")) {
            Log.w(TAG, "socketReadResponse: data value is not end with '#';");
            return;
        }
        messageString = resultBuilder.toString().split("#");
        int messageCount = messageString.length;
        for (int i = 0; i < messageCount; i++) {

            if (messageString[i].trim().equals("h") && pulseManager != null) {

                pulseManager.feed();
                continue;
            }
            parseString = messageString[i].split("@");
            if (parseString.length > 1) {
                try {
                    if(listener!=null)
                        listener.parseSocketCMD(Integer.parseInt(parseString[0]), parseString[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        resultBuilder.delete(0, resultBuilder.length());
    }

    private RemoteSocketHelperListener listener;

    public interface RemoteSocketHelperListener {
        void onSocketConnectionSuccess();

        void onSocketDisconnection();

        void onSocketConnectionFailed();

        void parseSocketCMD(int returncode, String returnmsg);
    }
}
