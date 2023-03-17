package com.example.wifidemo1.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifidemo1.adapter.BaseDataBindingAdapter;
import com.example.wifidemo1.adapter.DevicesListAdapter;
import com.example.wifidemo1.bluetooth.BlueToothUtil;
import com.example.wifidemo1.broadcaster.BlueToothReceiver;
import com.example.wifidemo1.broadcaster.BroadcasterUtil;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.permission.PermissionUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @author: fuhejian
 * @date: 2023/3/3
 */
public class PolarisUtil {

    static public PolarisUtil INSTANCE;

    static {
        INSTANCE = new PolarisUtil();
    }

    /**
     * 注册寻找指定名字BlueTooth的广播
     *
     * @param context       AppCompatActivity,用于绑定生命周期
     * @param adapter       找到该BLE时用于更新到该adapter
     * @param blueToothName 指定要寻找的BLE名字
     */
    @SuppressLint("MissingPermission")
    public void registerBlueTooth(AppCompatActivity context, ListAdapter<BluetoothDevice, RecyclerView.ViewHolder> adapter, String blueToothName, BlueToothUtil.WhenScanOnStop whenScanOnStop) {
        if (PermissionUtil.checkBlueToothCONNECT(context)) {
            BlueToothUtil.registerBlueToothReceiver((device, receiver,data) -> {//找到BLE时的回调,已过滤重复性device
                if (blueToothName != null && device.getName().startsWith(blueToothName)) {//找到后添加到adapter并注销广播
                    MyLog.printLog("当前类:PolarisUtil,信息:" + "找到当前的设备" + device.getName());
                    if (adapter instanceof BaseDataBindingAdapter) {
                        ((BaseDataBindingAdapter<BluetoothDevice>) adapter).addItem(device);
                    } else {
                        ArrayList<BluetoothDevice> list = new ArrayList<>(adapter.getCurrentList());
                        list.add(device);
                        adapter.submitList(list);
                    }
                } else if(blueToothName == null) {
                    MyLog.printLog("当前类:PolarisUtil,信息:" + "找到当前的设备" + device.getName());
                    if (adapter instanceof BaseDataBindingAdapter) {
                        data.forEach(it->{
                            ((BaseDataBindingAdapter<BluetoothDevice>) adapter).addItem(it);
                        });
                        data.clear();
                    } else {
                        ArrayList<BluetoothDevice> list = new ArrayList<>(adapter.getCurrentList());
                        list.add(device);
                        adapter.submitList(new ArrayList<>(data));
                    }
                }
            }, context, whenScanOnStop);
        }
    }

}
