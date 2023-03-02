package com.example.wifidemo1.brocaster;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Parcelable;
import android.util.ArraySet;

import androidx.core.app.ActivityCompat;

import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.permission.PermissionUtil;

import java.util.ArrayList;

public class BlueToothReceiver extends BroadcastReceiver {

    ReceiverListener mListener;

    ArraySet<String> mData = new ArraySet();

    public BlueToothReceiver(ReceiverListener listener) {
        mListener = listener;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case BluetoothDevice.ACTION_FOUND: {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!PermissionUtil.checkBlueToothCONNECT(context)) {
                    return;
                }
                String name = device.getName();
                if(name!=null){
                    MyLog.printLog(name);
                }
                if (name != null && mListener!=null) {
                    if (!mData.contains(name)) {
                        mListener.onAddData(device);
                        mData.add(name);
                    }
                }
            }
            default: {

            }
        }
    }

    public interface ReceiverListener{

        public void onAddData(BluetoothDevice device);

    }

}
