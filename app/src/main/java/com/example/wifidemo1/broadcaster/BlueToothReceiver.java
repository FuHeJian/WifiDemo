package com.example.wifidemo1.broadcaster;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.ArraySet;

import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.permission.PermissionUtil;


public class BlueToothReceiver extends BroadcastReceiver {

    ReceiverListener mListener;
    ArraySet<String> mData = new ArraySet<>();

    /**
     *
     * @param listener 当获取到蓝牙设备时进行回调
     */
    public BlueToothReceiver(ReceiverListener listener) {
        mListener = listener;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case BluetoothDevice.ACTION_FOUND: {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!PermissionUtil.checkBlueToothCONNECT(context)) {
                    return;
                }
                String name = device.getName();
                if(name!=null){
                    MyLog.printLog(name);
                }
                if (name != null && mListener!=null) {
                    if (!mData.contains(name)) {
                        mListener.onAddData(device,this);
                        mData.add(name);
                    }
                }
            }
            default: {

            }
        }
    }

    /**
     * 找到该BLE时的回调
     */
    public interface ReceiverListener{
        /**
         * 找到该BLE时的回调
         * @param device 找到的Device
         * @param receiver 接收此广播的BroadcastReceiver
         */
        void onAddData(BluetoothDevice device,BroadcastReceiver receiver);

    }

}
