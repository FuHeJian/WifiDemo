package com.example.wifidemo1.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.MacAddress;
import android.net.Network;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifidemo1.App;
import com.example.wifidemo1.BR;
import com.example.wifidemo1.Function.MyConsumer;
import com.example.wifidemo1.R;
import com.example.wifidemo1.bluetooth.BlueToothUtil;
import com.example.wifidemo1.databinding.BluetoothdeviceitemBinding;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.model.DeviceData;
import com.example.wifidemo1.network.NetCallBack.NetWorkCallbackAsync;
import com.example.wifidemo1.network.NetWorkUtil;
import com.example.wifidemo1.network.PolarisSettings;
import com.example.wifidemo1.oksocket.client.sdk.OkSocket;
import com.example.wifidemo1.oksocket.client.sdk.client.ConnectionInfo;
import com.example.wifidemo1.oksocket.client.sdk.client.action.ISocketActionListener;
import com.example.wifidemo1.oksocket.client.sdk.client.connection.IConnectionManager;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IPulseSendable;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.ISendable;
import com.example.wifidemo1.oksocket.core.pojo.OriginalData;
import com.example.wifidemo1.socket.PolarisSocketHelper;
import com.example.wifidemo1.socket.SocketUtil;
import com.example.wifidemo1.socket.sendable.PulseSendable;
import com.example.wifidemo1.utils.OrderCommunication;
import com.example.wifidemo1.viewmodel.HomeViewModel;
import com.example.wifidemo1.wifi.WifiUtil;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.nio.charset.Charset;
import java.security.PrivilegedAction;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;


public class DevicesListAdapter extends BaseDataBindingAdapter<BluetoothDevice> {

    private BluetoothGattCallback mSingleClickCallBack;
    private DeviceData mConnectedDeviceData = null;

    private BluetoothGatt mGatt;

    /**
     * 连接上的item
     */
    private View currentConnectClickView = null;

    /**
     * 点击的item
     */
    private View currentClickView = null;

    IConnectionManager oldSocket = null;

    private boolean blueInitiativeDisConnect = false;

    private boolean lastBlueConnectSuccess;


    public DevicesListAdapter(@NonNull DiffUtil.ItemCallback diffCallback, @NotNull LifecycleOwner lifecycleOwner) {
        super(diffCallback, lifecycleOwner);
    }

    private DevicesListAdapter(@NonNull AsyncDifferConfig config, @NotNull LifecycleOwner lifecycleOwner) {
        super(config, lifecycleOwner);
    }

    @Override
    public void onBindItem(ViewDataBinding binding, BluetoothDevice item, int position) {
        MyLog.printLog("当前类:DevicesListAdapter,位置信息:" + position);
        View view = binding.getRoot();

        if (binding instanceof BluetoothdeviceitemBinding) {
            ((BluetoothdeviceitemBinding) binding).setDevice(new DeviceData(item, ""));
        }

        view.setOnClickListener(new View.OnClickListener() {
            private boolean click = true;

            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                if (!click) return;
                click = false;
                lastBlueConnectSuccess = true;
                stopTimer();
                currentClickView = v;
                assert binding instanceof BluetoothdeviceitemBinding;
                if (((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentClickView)).getDevice() == mConnectedDeviceData) {
                    return;
                }
                if (mSingleClickCallBack == null) {
                    mSingleClickCallBack = new BluetoothGattCallback() {
                        private Runnable reconnectRun;

                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            super.onConnectionStateChange(gatt, status, newState);
                            lastBlueConnectSuccess = true;
                            switch (newState) {
                                case BluetoothGatt.STATE_CONNECTED: {
                                    {
                                        currentConnectClickView = currentClickView;
                                        //在recyclerView中更新蓝牙连接状态信息
                                        //===start====
                                        ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice().mStatus.set("蓝牙连接成功");
                                        //===end====

                                        //断开蓝牙
                                        blueInitiativeDisConnect = true;
//                                        gatt.disconnect();//断开后可以再调用gatt.connect()重连，在关闭会调用调用gatt.close()释放资源，不要在disconnect()后立即调用gatt.close()，会导致DISCONNECTED状态无法回调

                                        BluetoothDevice device = gatt.getDevice();
                                        String address = device.getAddress();
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                            MacAddress bssid = MacAddress.fromString(BlueToothUtil.INSTANCE.getMacAdd(address, 1));

                                            WifiUtil.INSTANCE.connectWifi(v.getContext(), device.getName(), bssid, new NetWorkCallbackAsync.AvailableNetworkListener() {
                                                /**
                                                 * 表示当前listener监听的设备
                                                 */
                                                private DeviceData currentListenerDeviceData;

                                                private boolean netWorkUpdate = false;

/*                                                @Override
                                                public void onSuccess(Network network, ConnectivityManager.NetworkCallback networkCallback) {

                                                    if (network == null) return;

                                                    //在recyclerView中更新wifi连接状态信息
                                                    //===start====
                                                    if (mConnectedDeviceData != null) {
                                                        mConnectedDeviceData.mStatus.set("");
                                                    }
                                                    //不能直接引用v,由于这个v是属于创建BluetoothGattCallback对象时引用的，当下一次点击事件来时，由于对象不会重建，v指向的对象内存还是之前创建对象引用的，所以对v指向的view操作不会引起当前点击的view的视图发生改变
                                                    //使用类私有属性提升作用域，使mConnectedDeviceData指向的内存地址是当前点击的view
//                                                    mConnectedDeviceData = ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice();
                                                    currentListenerDeviceData = ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice();
                                                    currentListenerDeviceData.mStatus.set("wifi连接成功");

                                                    NetWorkUtil.INSTANCE.setPolarisLanNetWork(network);//更新局域网配置
                                                    MyLog.printLog("当前类:DevicesListAdapter,当前方法：onSuccess,信息:netwok" + network);
                                                    //===end====

                                                    //将之后的socket都通过这个network进行连接，若通过network.bindSocket(socket),可以使该socket通过指定的network访问，不受bindProcessToNetwork影响。
                                                    //connectivityManager.bindProcessToNetwork(network);

                                                    //使用webSocket连接不上，所以选择OkSocket,websocket是应用层协议，需要服务器支持
                                                    oldSocket = SocketUtil.INSTANCE.connectSocket("192.168.0.1", 9090, network, (AppCompatActivity) v.getContext(), oldSocket, new ISocketActionListener() {

                                                        private int reconnectTimes = 0;
                                                        private int maxReconnectTimes = 3;

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
                                                            //boolean isPulse = Arrays.equals(originalData.getBodyBytes(), RequestCommands.Pulse);

                                                            PolarisSocketHelper.INSTANCE.dispatchCode(connectionInfo, s, originalData);

                                                            String value = new String(originalData.getBodyBytes(), Charset.forName("utf-8"));
                                                            MyLog.printLog("当前类:DevicesListAdapter,信息:" + value);
                                                            currentListenerDeviceData.mStatus.set("socket接收到数据：" + value);
                                                        }

                                                        @Override
                                                        public void onSocketWriteResponse(ConnectionInfo connectionInfo, String s, ISendable iSendable) {

                                                        }

                                                        @Override
                                                        public void onPulseSend(ConnectionInfo connectionInfo, IPulseSendable iPulseSendable) {
                                                            //心跳发送后回调
                                                            MyLog.printLog("onPulseSend");
                                                            try {
                                                                currentListenerDeviceData.mStatus.set("socket正在心跳");
                                                            } catch (Exception e) {
                                                                MyLog.printLog("当前类:DevicesListAdapter,当前方法：onPulseSend,信息:");
                                                            }
                                                        }

                                                        @Override
                                                        public void onSocketDisconnection(ConnectionInfo connectionInfo, String s, Exception e) {
                                                            //OkSocket.open(connectionInfo)通过缓存获取，缓存没有就会重新创建，所以要保证缓存中存在connectionInfo
                                                            MyLog.printLog("onSocketDisconnection");
                                                            currentListenerDeviceData.mStatus.set("socket断开连接");
                                                            //重连
                                                            *//*if (currentListenerDeviceData == ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice()) {
                                                                SocketUtil.INSTANCE.CurrentSocket = null;
                                                                OkSocket.open(connectionInfo).unRegisterReceiver(this);
                                                                currentConnectClickView.callOnClick();
                                                            }*//*

                                                            if (currentListenerDeviceData == ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice()) {
                                                                SocketUtil.INSTANCE.CurrentSocket.connect();
                                                            }

                                                        }

                                                        @Override
                                                        public void onSocketConnectionSuccess(ConnectionInfo connectionInfo, String s) {

                                                            //OkSocket.open(ConnectionInfo)表示从缓存中获取，OkSocket.open(String ip,int port)表示会重新创建一个
                                                            //由于PulseManager是connect后通过子线程设置的，如果connect后立即调用，getPulseManager可能返回空，但是连接成功是在PulseManager之后，所以这里获取一定不为空。

                                                            OkSocket.open(connectionInfo).getPulseManager().setPulseSendable(new PulseSendable()).pulse();

                                                            currentListenerDeviceData.mStatus.set("socket连接成功");
                                                            if (SocketUtil.INSTANCE.CurrentSocket == null || PolarisSettings.RomUpgradeIsSuccess) {
                                                                SocketUtil.INSTANCE.CurrentSocket = OkSocket.open(connectionInfo);
                                                                OrderCommunication.getInstance().SP_GET_DEVICE_VERSION();
                                                            }
                                                            SocketUtil.INSTANCE.CurrentSocket = OkSocket.open(connectionInfo);
                                                            MyLog.printLog("onSocketConnectionSuccess");

                                                            mConnectedDeviceData = ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice();

                                                        }

                                                        @Override
                                                        public void onSocketConnectionFailed(ConnectionInfo connectionInfo, String s, Exception e) {
                                                            MyLog.printLog("onSocketConnectionFailed");
                                                            //重连
                                                            *//*if (currentListenerDeviceData == ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice()) {
                                                                SocketUtil.INSTANCE.CurrentSocket = null;
                                                                currentConnectClickView.callOnClick();
                                                            }*//*
                                                            blueInitiativeDisConnect = false;
                                                            //重连失败，开始彻底重连

                                                            if (netWorkUpdate) {

                                                            } else {
                                                                ConnectivityManager c = (ConnectivityManager) App.GlobalManager.INSTANCE.getContext(null).getSystemService(Context.CONNECTIVITY_SERVICE);
                                                                c.unregisterNetworkCallback(networkCallback);//每次连接只使用一个networkCallback，若调用unregisterNetworkCallback会断开此次请求的network连接
                                                                blueInitiativeDisConnect = false;
                                                                MyLog.printLog("当前类:DevicesListAdapter,当前方法：accept,当前线程:" + Thread.currentThread().getName() + ",信息:" + s);
                                                                ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice().mStatus.set("开始重连");
                                                                if (currentListenerDeviceData == ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice()) {
                                                                    mConnectedDeviceData = null;
                                                                    if (SocketUtil.INSTANCE.CurrentSocket != null) {
                                                                        OkSocket.open(connectionInfo).unRegisterReceiver(this);
                                                                    }
                                                                    MyLog.printLog("当前类:DevicesListAdapter,当前方法：onSocketConnectionFailed,当前线程:" + Thread.currentThread().getName() + ",信息:socket连接失败，尝试点击连接");
                                                                    currentConnectClickView.callOnClick();
                                                                }
                                                            }
                                                        }
                                                    });
                                                }*/

                                                //网络更新，重新连接socket
                                                @Override
                                                public void onUpdate(Network network) {
                                                    super.onUpdate(network);
                                                    if (network == null) return;

                                                    //在recyclerView中更新wifi连接状态信息
                                                    //===start====
                                                    if (mConnectedDeviceData != null) {
                                                        mConnectedDeviceData.mStatus.set("");
                                                    }
                                                    //不能直接引用v,由于这个v是属于创建BluetoothGattCallback对象时引用的，当下一次点击事件来时，由于对象不会重建，v指向的对象内存还是之前创建对象引用的，所以对v指向的view操作不会引起当前点击的view的视图发生改变
                                                    //使用类私有属性提升作用域，使mConnectedDeviceData指向的内存地址是当前点击的view
//                                                    mConnectedDeviceData = ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice();
                                                    currentListenerDeviceData = ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice();
                                                    currentListenerDeviceData.mStatus.set("wifi连接成功");

                                                    NetWorkUtil.INSTANCE.setPolarisLanNetWork(network);//更新局域网配置
                                                    MyLog.printLog("当前类:DevicesListAdapter,当前方法：onSuccess,信息:netwok" + network);
                                                    //===end====

                                                    //将之后的socket都通过这个network进行连接，若通过network.bindSocket(socket),可以使该socket通过指定的network访问，不受bindProcessToNetwork影响。
                                                    //connectivityManager.bindProcessToNetwork(network);
                                                    oldSocket = SocketUtil.INSTANCE.connectSocket("192.168.0.1", 9090, network, (AppCompatActivity) v.getContext(), oldSocket, new ISocketActionListener() {

                                                        @Override
                                                        public void onSocketIOThreadStart(String s) {
                                                            MyLog.printLog("onSocketIOThreadStart");
                                                        }

                                                        @Override
                                                        public void onSocketIOThreadShutdown(String s, Exception e) {
                                                            MyLog.printLog("当前类:DevicesListAdapter,当前方法：onSocketIOThreadShutdown,当前线程:"+ Thread.currentThread().getName()+",信息:" + e.getMessage());
                                                        }

                                                        @Override
                                                        public void onSocketReadResponse(ConnectionInfo connectionInfo, String s, OriginalData originalData) {

                                                            //心跳处理，防止自主断开
                                                            //boolean isPulse = Arrays.equals(originalData.getBodyBytes(), RequestCommands.Pulse);

                                                            PolarisSocketHelper.INSTANCE.dispatchCode(connectionInfo, s, originalData);

                                                            String value = new String(originalData.getBodyBytes(), Charset.forName("utf-8"));
                                                            MyLog.printLog("当前类:DevicesListAdapter,信息:" + value);
                                                            currentListenerDeviceData.mStatus.set("socket接收到数据：" + value);

                                                        }

                                                        @Override
                                                        public void onSocketWriteResponse(ConnectionInfo connectionInfo, String s, ISendable iSendable) {

                                                        }

                                                        @Override
                                                        public void onPulseSend(ConnectionInfo connectionInfo, IPulseSendable iPulseSendable) {
                                                            //心跳发送后回调
                                                            MyLog.printLog("onPulseSend");
                                                            try {
                                                                currentListenerDeviceData.mStatus.set("socket正在心跳");
                                                            } catch (Exception e) {
                                                                MyLog.printLog("当前类:DevicesListAdapter,当前方法：onPulseSend,信息:");
                                                            }
                                                        }

                                                        @Override
                                                        public void onSocketDisconnection(ConnectionInfo connectionInfo, String s, Exception e) {
                                                            //OkSocket.open(connectionInfo)通过缓存获取，缓存没有就会重新创建，所以要保证缓存中存在connectionInfo
                                                            MyLog.printLog("onSocketDisconnection");
                                                            currentListenerDeviceData.mStatus.set("socket断开连接");
                                                            //重连
                                                            /*if (currentListenerDeviceData == ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice()) {
                                                                SocketUtil.INSTANCE.CurrentSocket = null;
                                                                OkSocket.open(connectionInfo).unRegisterReceiver(this);
                                                                currentConnectClickView.callOnClick();
                                                            }*/
                                                            blueInitiativeDisConnect = false;
                                                            MyLog.printLog("当前类:DevicesListAdapter,当前方法：accept,当前线程:" + Thread.currentThread().getName() + ",信息:" + s);
                                                            ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice().mStatus.set("开始重连");
                                                            //重连
                                                            if (currentListenerDeviceData == ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice()) {
                                                                SocketUtil.INSTANCE.CurrentSocket.connect();
                                                            }
                                                        }

                                                        @Override
                                                        public void onSocketConnectionSuccess(ConnectionInfo connectionInfo, String s) {

                                                            //OkSocket.open(ConnectionInfo)表示从缓存中获取，OkSocket.open(String ip,int port)表示会重新创建一个
                                                            //由于PulseManager是connect后通过子线程设置的，如果connect后立即调用，getPulseManager可能返回空，但是连接成功是在PulseManager之后，所以这里获取一定不为空。

                                                            OkSocket.open(connectionInfo).getPulseManager().setPulseSendable(new PulseSendable()).pulse();

                                                            currentListenerDeviceData.mStatus.set("socket连接成功");
                                                            if (SocketUtil.INSTANCE.CurrentSocket == null || PolarisSettings.RomUpgradeIsSuccess) {
                                                                SocketUtil.INSTANCE.CurrentSocket = OkSocket.open(connectionInfo);
                                                                OrderCommunication.getInstance().SP_GET_DEVICE_VERSION();
                                                                OrderCommunication.getInstance().SP_SOCKET_CLIENT_TYPE(false);
                                                            }
                                                            SocketUtil.INSTANCE.CurrentSocket = OkSocket.open(connectionInfo);
                                                            MyLog.printLog("onSocketConnectionSuccess");

                                                            mConnectedDeviceData = ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice();

                                                        }

                                                        @Override
                                                        public void onSocketConnectionFailed(ConnectionInfo connectionInfo, String s, Exception e) {
                                                            MyLog.printLog("onSocketConnectionFailed");
                                                            //重连
                                                            /*if (currentListenerDeviceData == ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice()) {
                                                                SocketUtil.INSTANCE.CurrentSocket = null;
                                                                currentConnectClickView.callOnClick();
                                                            }*/
                                                            blueInitiativeDisConnect = false;
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onLost(Network network,ConnectivityManager.NetworkCallback networkCallback) {
                                                    //wifi断开，这里执行重连
                                                    //开始彻底重连
                                                    if (currentListenerDeviceData == ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentConnectClickView)).getDevice()) {
                                                        mConnectedDeviceData = null;
                                                        MyLog.printLog("当前类:DevicesListAdapter,当前方法：onSocketConnectionFailed,当前线程:"+ Thread.currentThread().getName()+",信息:socket连接失败，尝试点击连接");
                                                        ConnectivityManager c = (ConnectivityManager) App.GlobalManager.INSTANCE.getContext(null).getSystemService(Context.CONNECTIVITY_SERVICE);
                                                        c.unregisterNetworkCallback(networkCallback);//每次连接只使用一个networkCallback，若调用unregisterNetworkCallback会断开此次请求的network连接
                                                        currentConnectClickView.callOnClick();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                                case BluetoothGatt.STATE_DISCONNECTED: {//无法连接，断开连接都会走这里
                                    if (blueInitiativeDisConnect) {
                                        gatt.close();
                                        click = true;
                                        reconnectRun = null;
                                    } else {
                                        //第一次连接就没有连上不需要重连
                                        if (currentConnectClickView == null || currentClickView != currentConnectClickView) {
                                            if (mGatt != null) {
                                                mGatt.close();
                                            }
                                            return;
                                        }
                                        //重新连接
                                        if (mGatt != null) {
                                            mGatt.close();
                                        }
                                        if (reconnectRun == null) {
                                            reconnectRun = new Runnable() {
                                                @Override
                                                public void run() {
                                                    MyLog.printLog("当前类:DevicesListAdapter,当前方法：onConnectionStateChange,当前线程:" + Thread.currentThread().getName() + ",信息:蓝牙重连");
                                                    mGatt = gatt;
                                                    currentClickView.callOnClick();//重连
                                                }
                                            };
                                        }
                                        click = true;
                                        if (lastBlueConnectSuccess) {
                                            MyLog.printLog("当前类:DevicesListAdapter,当前方法：onConnectionStateChange,当前线程:" + Thread.currentThread().getName() + ",信息:重连线程被调用");
                                            AndroidSchedulers.mainThread().scheduleDirect(reconnectRun, 2, TimeUnit.SECONDS);
                                            lastBlueConnectSuccess = false;
                                        }
                                    }
                                }
                                default: {

                                }
                            }
                        }
                    };
                }

                //autoConnect设置为false,表示直接连接，如果设为true的话表示要device可用的时候再连接,因为device即使可用也可能没有设置可用标识。
                item.connectGatt(view.getContext(), false, mSingleClickCallBack);

            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.bluetoothdeviceitem;
    }

}
