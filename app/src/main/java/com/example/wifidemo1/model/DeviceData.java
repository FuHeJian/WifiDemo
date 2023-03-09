package com.example.wifidemo1.model;

import android.bluetooth.BluetoothDevice;

import androidx.databinding.BaseObservable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

/**
 * @author: fuhejian
 * @date: 2023/3/6
 */
public class DeviceData extends BaseObservable {

    public DeviceData(BluetoothDevice device,String status){
        mDevice.set(device);
        mStatus.set(status);
    }

    public ObservableField<BluetoothDevice> mDevice = new ObservableField<>();
    public ObservableField<String> mStatus = new ObservableField<>();

}
