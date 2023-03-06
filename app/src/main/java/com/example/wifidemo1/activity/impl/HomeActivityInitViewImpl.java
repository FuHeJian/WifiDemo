package com.example.wifidemo1.activity.impl;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wifidemo1.activity.HomeActivity;
import com.example.wifidemo1.activity.PolarisUtil;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.adapter.DevicesListAdapter;
import com.example.wifidemo1.databinding.ActivityMainBinding;
import com.example.wifidemo1.permission.PermissionUtil;

/**
 * @author: fuhejian
 * @date: 2023/3/6
 */
public class HomeActivityInitViewImpl implements InitView<ActivityMainBinding> {

    @Override
    public void initView(ActivityMainBinding binding) {
        //将之后的socket都通过这个network进行连接，若通过network.bindSocket(socket),可以使该socket通过指定的network访问，不受bindProcessToNetwork影响。
        //connectivityManager.bindProcessToNetwork(network);
        binding.DevicesList.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.VERTICAL, false));

        DevicesListAdapter adapter = new DevicesListAdapter(
                new DiffUtil.ItemCallback<BluetoothDevice>() {
                    @SuppressLint("MissingPermission")
                    public boolean areItemsTheSame(
                            @NonNull BluetoothDevice oldItem,
                            @NonNull BluetoothDevice newItem
                    ) {
                        if (!PermissionUtil.checkBlueToothCONNECT(binding.getRoot().getContext())) {
                            return true;
                        }
                        if (oldItem.getName() != null && newItem.getName() != null) {
                            return oldItem.getName().equals(newItem.getName());
                        }
                        return false;
                    }

                    @SuppressLint("MissingPermission")
                    public boolean areContentsTheSame(
                            @NonNull BluetoothDevice oldItem,
                            @NonNull BluetoothDevice newItem
                    ) {
                        if (!PermissionUtil.checkBlueToothCONNECT(binding.getRoot().getContext())) {
                            return true;
                        }
                        if (oldItem.getName() != null && newItem.getName() != null) {
                            return oldItem.getName().equals(newItem.getName());
                        }
                        return false;
                    }
                });
        binding.DevicesList.setAdapter(adapter);

        //注册寻找polaris_2d3b07的该广播
        PolarisUtil.INSTANCE.registerBlueTooth((HomeActivity) binding.getRoot().getContext(), adapter, "polaris_2d3b07");
    }
}
