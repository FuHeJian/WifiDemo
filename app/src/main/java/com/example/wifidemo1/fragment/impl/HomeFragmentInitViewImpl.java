package com.example.wifidemo1.fragment.impl;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wifidemo1.activity.HomeActivity;
import com.example.wifidemo1.activity.PolarisUtil;
import com.example.wifidemo1.adapter.DevicesListAdapter;
import com.example.wifidemo1.databinding.HomeFragmentBinding;
import com.example.wifidemo1.fragment.i.FragmentInitView;
import com.example.wifidemo1.helper.BlueToothScanHelper;
import com.example.wifidemo1.permission.PermissionUtil;

/**
 * @author: fuhejian
 * @date: 2023/3/8
 */
public class HomeFragmentInitViewImpl implements FragmentInitView<HomeFragmentBinding> {

    @Override
    public void initView(HomeFragmentBinding binding,LifecycleOwner lifecycleOwner) {
        //将之后的socket都通过这个network进行连接，若通过network.bindSocket(socket),可以使该socket通过指定的network访问，不受bindProcessToNetwork影响。
        //connectivityManager.bindProcessToNetwork(network);
        binding.DevicesList.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.VERTICAL, false));
        DevicesListAdapter adapter;
        //横竖屏切换，保存数据
        if (binding.getViewModel() != null && binding.getViewModel().devicesListAdapter != null) {
            binding.setViewModel(binding.getViewModel());
            binding.DevicesList.setAdapter(binding.getViewModel().devicesListAdapter);
            adapter = binding.getViewModel().devicesListAdapter;
        } else {
            adapter = new DevicesListAdapter(new DiffUtil.ItemCallback<BluetoothDevice>() {
                @SuppressLint("MissingPermission")
                public boolean areItemsTheSame(@NonNull BluetoothDevice oldItem, @NonNull BluetoothDevice newItem) {
                    if (!PermissionUtil.checkBlueToothCONNECT(binding.getRoot().getContext())) {
                        return true;
                    }
                    if (oldItem.getName() != null && newItem.getName() != null) {
                        try {
                            //BluetoothDevice此时可能被释放导致异常
                            return oldItem.getName().equals(newItem.getName());
                        } catch (Exception e) {
                            return true;
                        }
                    }
                    return true;
                }

                @SuppressLint("MissingPermission")
                public boolean areContentsTheSame(@NonNull BluetoothDevice oldItem, @NonNull BluetoothDevice newItem) {
                    if (!PermissionUtil.checkBlueToothCONNECT(binding.getRoot().getContext())) {
                        return true;
                    }
                    if (oldItem.getName() != null && newItem.getName() != null) {
                        try {
                            //BluetoothDevice此时可能被释放导致异常
                            return oldItem.getName().equals(newItem.getName());
                        } catch (Exception e) {
                            return true;
                        }
                    }
                    return true;
                }
            },lifecycleOwner);
            binding.DevicesList.setAdapter(adapter);
            if (binding.getViewModel() != null) {
                binding.getViewModel().devicesListAdapter = adapter;
            }

            //注册寻找polaris_2d3b07的该广播
            String bleStartName = "polaris";
            PolarisUtil.INSTANCE.registerBlueTooth((HomeActivity) binding.getRoot().getContext(), adapter, null, bluetoothAdapter -> {
                adapter.stopTimer();//必须调用
                Toast.makeText(binding.getRoot().getContext(), "蓝牙扫描结束！", Toast.LENGTH_SHORT).show();
            });
        }

        binding.loadBLE.setOnClickListener(new View.OnClickListener() {
            private  boolean canClick = false;
            @Override
            public void onClick(View v) {
                if(canClick){
                    canClick = false;
                    BlueToothScanHelper.scanBLE(v.getContext(), bluetoothAdapter -> {
                        canClick = true;
                        adapter.stopTimer();
                        Toast.makeText(binding.getRoot().getContext(), "蓝牙扫描结束！", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

    }

}
