package com.example.wifidemo1.singleton;

import android.util.Log;

import com.example.wifidemo1.model.BroadcastActionEvent;
import com.example.wifidemo1.utils.MyMessage;
import com.example.wifidemo1.utils.OrderCommunication;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by LQF on 2019/12/17.
 */

public class SocketHelper {
    private static final String TAG = "SocketHelper";
    private static SocketHelper socketHelper;

    public void setRemote(boolean remote) {
        isRemote = remote;
    }

    private boolean isRemote;

    private RemoteSocketHelper remoteSocketHelper;
    private ShortRangeSocketHelper shortRangeSocketHelper;

    public String socketIP;
    public int socketPORT;
    public String resourceAddress;
    public String streamAddress;


    public static SocketHelper getInstance() {
        if (socketHelper == null) {
            synchronized (SocketHelper.class) {
                if (socketHelper == null) {
                    socketHelper = new SocketHelper();
                }
            }
        }
        return socketHelper;
    }

    private SocketHelper() {
        remoteSocketHelper = new RemoteSocketHelper(new RemoteSocketHelper.RemoteSocketHelperListener() {
            @Override
            public void onSocketConnectionSuccess() {
                if (isRemote)
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_SOCKET_CONNECT, true));
            }

            @Override
            public void onSocketDisconnection() {
                if (isRemote)
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_SOCKET_CONNECT, false));
            }

            @Override
            public void onSocketConnectionFailed() {
                if (isRemote)
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_SOCKET_CONNECT, false));
            }

            @Override
            public void parseSocketCMD(int returncode, String returnmsg) {
                SocketReturnMessage(true, returncode, returnmsg);
            }
        });
        shortRangeSocketHelper = new ShortRangeSocketHelper(new ShortRangeSocketHelper.ShortRangeSocketHelperListener() {
            @Override
            public void onSocketConnectionSuccess() {
                if (!isRemote)
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_SOCKET_CONNECT, true));
            }

            @Override
            public void onSocketDisconnection() {
                if (!isRemote)
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_SOCKET_CONNECT, false));
            }

            @Override
            public void onSocketConnectionFailed() {
                if (!isRemote)
                    EventBus.getDefault().post(new BroadcastActionEvent(MyMessage.ACTION_SOCKET_CONNECT, false));
            }

            @Override
            public void parseSocketCMD(int returncode, String returnmsg) {
                SocketReturnMessage(false, returncode, returnmsg);
            }
        });
    }


    public void initConnectAddress(boolean is4G, String socketIP, int socketPORT, String resourceAddress, String streamAddress) {
        this.isRemote = is4G;
        Log.d(TAG, "adsfdasfs initConnectAddress: is4G =" + is4G + ",socketIP =" + socketIP + ",socketPORT =" + socketPORT + ",resourceAddress =" + resourceAddress + ",streamAddress =" + streamAddress);
        if (is4G) {
            this.socketIP = socketIP;
            this.socketPORT = socketPORT;
            this.resourceAddress = resourceAddress;
            this.streamAddress = streamAddress;
        } else {
            this.socketIP = "192.168.0.1";
            this.socketPORT = 9090;
            this.resourceAddress = "http://192.168.0.1/";
            this.streamAddress = "http://192.168.0.1:8080/?action=stream";
        }
    }


    public boolean sockerIsConnect() {
        if (isRemote) {
            return remoteSocketHelper.sockerIsConnect();
        } else {
            return shortRangeSocketHelper.sockerIsConnect();
        }
    }


    public void connectSocket() {
        Log.d(TAG, "connectSocket: isRemote =" + isRemote + ",socketIP =" + socketIP);
        try {
            if (isRemote) {
                remoteSocketHelper.connectSocket(socketIP, socketPORT);
            } else {
                shortRangeSocketHelper.connectSocket(socketIP, socketPORT);
            }
        } catch (Exception e) {

        }

    }


    public void disConnectSocket() {
        if (isRemote) {
            remoteSocketHelper.disConnectSocket();
        } else {
            shortRangeSocketHelper.disConnectSocket();


        }
    }

    public void sendMessage(String msg) throws Exception {
        if (isRemote) {
            remoteSocketHelper.sendMessage(msg);
        } else {
            shortRangeSocketHelper.sendMessage(msg);
        }
    }

    public synchronized void SocketReturnMessage(boolean remote, int returncode, String returnmsg) {
        if (remote != isRemote)
            return;
        OrderCommunication.getInstance().parseSocketCMD(returncode, returnmsg);
    }
}
