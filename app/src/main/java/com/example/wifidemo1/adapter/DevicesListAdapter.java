package com.example.wifidemo1.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.net.MacAddress;
import android.net.Network;
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

import com.example.wifidemo1.BR;
import com.example.wifidemo1.R;
import com.example.wifidemo1.bluetooth.BlueToothUtil;
import com.example.wifidemo1.databinding.BluetoothdeviceitemBinding;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.model.DeviceData;
import com.example.wifidemo1.network.NetCallBack.NetWorkCallbackAsync;
import com.example.wifidemo1.oksocket.client.sdk.OkSocket;
import com.example.wifidemo1.oksocket.client.sdk.client.ConnectionInfo;
import com.example.wifidemo1.oksocket.client.sdk.client.action.ISocketActionListener;
import com.example.wifidemo1.oksocket.client.sdk.client.connection.IConnectionManager;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.IPulseSendable;
import com.example.wifidemo1.oksocket.core.iocore.interfaces.ISendable;
import com.example.wifidemo1.oksocket.core.pojo.OriginalData;
import com.example.wifidemo1.socket.SocketUtil;
import com.example.wifidemo1.socket.sendable.PulseSendable;
import com.example.wifidemo1.viewmodel.HomeViewModel;
import com.example.wifidemo1.wifi.WifiUtil;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.security.PrivilegedAction;
import java.util.UUID;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.FlowableOnSubscribe;


public class DevicesListAdapter extends BaseDataBindingAdapter<BluetoothDevice> {

    private BluetoothGattCallback mSingleClickCallBack;
    private DeviceData mConnectedDeviceData = null;

    private View currentClickView = null;

    IConnectionManager oldSocket = null;

    public DevicesListAdapter(@NonNull DiffUtil.ItemCallback diffCallback,@NotNull LifecycleOwner lifecycleOwner) {
        super(diffCallback,lifecycleOwner);
    }

    private DevicesListAdapter(@NonNull AsyncDifferConfig config,@NotNull LifecycleOwner lifecycleOwner) {
        super(config,lifecycleOwner);
    }

    @Override
    void onBindItem(ViewDataBinding binding, BluetoothDevice item, int position) {
        MyLog.printLog("当前类:DevicesListAdapter,位置信息:" + position);
        View view = binding.getRoot();

        if (binding instanceof BluetoothdeviceitemBinding) {
            ((BluetoothdeviceitemBinding) binding).setDevice(new DeviceData(item, ""));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                currentClickView = v;
                assert binding instanceof BluetoothdeviceitemBinding;
                if (((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentClickView)).getDevice() == mConnectedDeviceData) {
                    return;
                }
                if (mSingleClickCallBack == null) {
                    mSingleClickCallBack = new BluetoothGattCallback() {
                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            super.onConnectionStateChange(gatt, status, newState);
                            switch (newState) {
                                case BluetoothGatt.STATE_CONNECTED: {
                                    {
                                        //在recyclerView中更新蓝牙连接状态信息
                                        //===start====
                                        ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentClickView)).getDevice().mStatus.set("蓝牙连接成功");
                                        //===end====

                                        BluetoothDevice device = gatt.getDevice();
                                        String address = device.getAddress();
                                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                            MacAddress bssid = MacAddress.fromString(BlueToothUtil.INSTANCE.getMacAdd(address, 1));
                                            //断开蓝牙
                                            gatt.disconnect();//断开后可以再调用gatt.connect()重连，在关闭会调用调用gatt.close()释放资源，不要在disconnect()后立即调用gatt.close()，会导致DISCONNECTED状态无法回调
                                            WifiUtil.INSTANCE.connectWifi(v.getContext(), device.getName(), bssid, new NetWorkCallbackAsync.AvailableNetworkListener() {
                                                /**
                                                 * 表示当前listener监听的设备
                                                 */
                                                private DeviceData currentListenerDeviceData;

                                                @Override
                                                public void onSuccess(Network network) {

                                                    //在recyclerView中更新wifi连接状态信息
                                                    //===start====
/*                                                    if (mConnectedDeviceData != null) {
                                                        mConnectedDeviceData.mStatus.set("");
                                                    }*/
                                                    //不能直接引用v,由于这个v是属于创建BluetoothGattCallback对象时引用的，当下一次点击事件来时，由于对象不会重建，v指向的对象内存还是之前创建对象引用的，所以对v指向的view操作不会引起当前点击的view的视图发生改变
                                                    //使用类私有属性提升作用域，使mConnectedDeviceData指向的内存地址是当前点击的view
//                                                    mConnectedDeviceData = ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentClickView)).getDevice();
                                                    currentListenerDeviceData = ((BluetoothdeviceitemBinding) DataBindingUtil.getBinding(currentClickView)).getDevice();
                                                    ;
                                                    currentListenerDeviceData.mStatus.set("wifi连接成功");
                                                    //===end====

                                                    //将之后的socket都通过这个network进行连接，若通过network.bindSocket(socket),可以使该socket通过指定的network访问，不受bindProcessToNetwork影响。
                                                    //connectivityManager.bindProcessToNetwork(network);

                                                    //使用webSocket连接不上，所以选择OkSocket,websocket是应用层协议，需要服务器支持
                                                    oldSocket = SocketUtil.INSTANCE.connectSocket("192.168.0.1", 9090, network, (AppCompatActivity) v.getContext(), oldSocket, new ISocketActionListener() {
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
                                                            //if (isPulse) {

                                                            OkSocket.open(connectionInfo).getPulseManager().feed();//表示收到服务器的心跳，不会尝试断开连接
                                                            //}

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
                                                            currentListenerDeviceData.mStatus.set("socket正在心跳");
                                                        }

                                                        @Override
                                                        public void onSocketDisconnection(ConnectionInfo connectionInfo, String s, Exception e) {
                                                            //OkSocket.open(connectionInfo)通过缓存获取，缓存没有就会重新创建，所以要保证缓存中存在connectionInfo
                                                            MyLog.printLog("onSocketDisconnection");
                                                            currentListenerDeviceData.mStatus.set("socket断开连接");
                                                            currentListenerDeviceData = null;
                                                        }

                                                        @Override
                                                        public void onSocketConnectionSuccess(ConnectionInfo connectionInfo, String s) {

                                                            //OkSocket.open(ConnectionInfo)表示从缓存中获取，OkSocket.open(String ip,int port)表示会重新创建一个
                                                            //由于PulseManager是connect后通过子线程设置的，如果connect后立即调用，getPulseManager可能返回空，但是连接成功是在PulseManager之后，所以这里获取一定不为空。
                                                            //TODO : OkSocket.open(connectionInfo) 返回的 和 上面生成的不一样，应该是缓存中没有放置生成的socket
                                                            OkSocket.open(connectionInfo).getPulseManager().setPulseSendable(new PulseSendable()).pulse();

                                                            currentListenerDeviceData.mStatus.set("socket连接成功");
                                                            MyLog.printLog("onSocketConnectionSuccess");

                                                        }

                                                        @Override
                                                        public void onSocketConnectionFailed(ConnectionInfo connectionInfo, String s, Exception e) {
                                                            MyLog.printLog("onSocketConnectionFailed");
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    }
                                }
                                case BluetoothGatt.STATE_DISCONNECTED: {
                                    gatt.close();//彻底关闭客户端，无法再通过gatt.connect()重连
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
    int getLayoutId() {
        return R.layout.bluetoothdeviceitem;
    }

}
