package com.example.wifidemo1.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.net.MacAddress;
import android.net.Network;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;

import com.example.wifidemo1.R;
import com.example.wifidemo1.bluetooth.BlueToothUtil;
import com.example.wifidemo1.databinding.BluetoothdeviceitemBinding;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.network.NetCallBack.NetWorkCallbackAsync;
import com.example.wifidemo1.socket.SocketUtil;
import com.example.wifidemo1.wifi.WifiUtil;

public class DevicesListAdapter extends BaseDataBindingAdapter<BluetoothDevice> {

    private BluetoothGattCallback mSingleClickCallBack;

    public DevicesListAdapter(@NonNull DiffUtil.ItemCallback diffCallback) {
        super(diffCallback);
    }

    public DevicesListAdapter(@NonNull AsyncDifferConfig config) {
        super(config);
    }

    @Override
    void onBindItem(ViewDataBinding binding, BluetoothDevice item, int position) {

        View view = binding.getRoot();

        if (binding instanceof BluetoothdeviceitemBinding) {
            ((BluetoothdeviceitemBinding) binding).setDevice(item);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {

                if (mSingleClickCallBack == null) {
                    mSingleClickCallBack = new BluetoothGattCallback() {
                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            super.onConnectionStateChange(gatt, status, newState);
                            if (newState == BluetoothGatt.GATT_SUCCESS) {
                                MyLog.printLog("蓝牙连接成功");
                                BluetoothDevice device = gatt.getDevice();
                                String address = device.getAddress();
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                    MacAddress bssid = MacAddress.fromString(BlueToothUtil.INSTANCE.getMacAdd(address, 1));
                                    WifiUtil.INSTANCE.connectWifi(v.getContext(), device.getName(), bssid, new NetWorkCallbackAsync.AvailableNetworkListener() {
                                        @Override
                                        public void onSuccess(Network network) {

                                            //断开蓝牙
                                            gatt.disconnect();

                                            //将之后的socket都通过这个network进行连接，若通过network.bindSocket(socket),可以使该socket通过指定的network访问，不受bindProcessToNetwork影响。
//                                            connectivityManager.bindProcessToNetwork(network);

                                            //使用webSocket连接不上，所以选择OkSocket,websocket是应用层协议，需要服务器支持
                                            SocketUtil.INSTANCE.connectSocket("192.168.0.1", 9090, network, (AppCompatActivity) v.getContext());

                                        }
                                    });
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
