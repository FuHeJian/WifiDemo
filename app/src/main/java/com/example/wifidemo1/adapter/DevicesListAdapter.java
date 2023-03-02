package com.example.wifidemo1.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.pm.PackageManager;
import android.net.MacAddress;
import android.net.Network;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;

import com.example.wifidemo1.R;
import com.example.wifidemo1.bluetooth.BlueToothUtil;
import com.example.wifidemo1.databinding.BluetoothdeviceitemBinding;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.network.NetCallBack.NetWorkCallbackAsync;
import com.example.wifidemo1.wifi.WifiUtil;

public class DevicesListAdapter extends BaseDataBindingAdapter<BluetoothDevice> {

    public DevicesListAdapter(@NonNull DiffUtil.ItemCallback diffCallback) {
        super(diffCallback);
    }

    public DevicesListAdapter(@NonNull AsyncDifferConfig config) {
        super(config);
    }

    @Override
    void onBindItem(ViewDataBinding binding, BluetoothDevice item, int position) {

        View view = binding.getRoot();

        if(binding instanceof BluetoothdeviceitemBinding)
        {
            ((BluetoothdeviceitemBinding) binding).setDevice(item);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {

                item.connectGatt(view.getContext(), false, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        if (newState == BluetoothGatt.GATT_SUCCESS) {
                            MyLog.printLog("连接成功");
                            BluetoothDevice device = gatt.getDevice();
                            String address = device.getAddress();
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                MacAddress bssid = MacAddress.fromString(BlueToothUtil.INSTANCE.getMacAdd(address,1));
                                WifiUtil.INSTANCE.connectWifi(v.getContext(), device.getName(), bssid, new NetWorkCallbackAsync.AvailableNetworkListener() {
                                    @Override
                                    public void onSuccess(Network network) {
                                            MyLog.printLog("AvailableNetworkListener异步onSuccess");
                                    }

                                    @Override
                                    public void onUpdate(Network network) {
                                        MyLog.printLog("AvailableNetworkListener异步onUpdate");
                                    }

                                    @Override
                                    public void onNotMatchNeedNetWork(Network network) {
                                        MyLog.printLog("AvailableNetworkListener异步onNotMatchNeedNetWork");
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        super.onServicesDiscovered(gatt, status);
                    }
                });

            }
        });
    }

    @Override
    int getLayoutId() {
        return R.layout.bluetoothdeviceitem;
    }

}
