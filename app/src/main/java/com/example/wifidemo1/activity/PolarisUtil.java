package com.example.wifidemo1.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.IntentFilter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifidemo1.bluetooth.BlueToothUtil;
import com.example.wifidemo1.broadcaster.BlueToothReceiver;
import com.example.wifidemo1.broadcaster.BroadcasterUtil;
import com.example.wifidemo1.permission.PermissionUtil;

import java.util.ArrayList;

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
    public void registerBlueTooth(AppCompatActivity context, ListAdapter<BluetoothDevice, RecyclerView.ViewHolder> adapter, String blueToothName) {
        if (PermissionUtil.checkBlueToothCONNECT(context)) {
            BlueToothUtil.registerBlueToothReceiver((device, receiver) -> {//找到BLE时的回调
                if (blueToothName!=null&&blueToothName.equals(device.getName())) {//找到后添加到adapter并注销广播
                    ArrayList<BluetoothDevice> list = new ArrayList<>();
                    list.add(device);
                    adapter.submitList(list);

                    //找到后注销掉广播
                    context.unregisterReceiver(receiver);
                }else {
                    ArrayList<BluetoothDevice> list = new ArrayList<>();
                    list.add(device);
                    adapter.submitList(list);
                }
            }, context);
        }
    }

}
