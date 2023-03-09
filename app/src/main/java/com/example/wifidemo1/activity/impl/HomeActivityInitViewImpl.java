package com.example.wifidemo1.activity.impl;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.wifidemo1.activity.ContextUtil;
import com.example.wifidemo1.activity.HomeActivity;
import com.example.wifidemo1.activity.PolarisUtil;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.adapter.BaseDataBindingFragmentAdapter;
import com.example.wifidemo1.adapter.DevicesListAdapter;
import com.example.wifidemo1.adapter.ViewPager2FragmentAdapter;
import com.example.wifidemo1.bluetooth.BlueToothUtil;
import com.example.wifidemo1.databinding.ActivityMainBinding;
import com.example.wifidemo1.fragment.HomeFragment;
import com.example.wifidemo1.helper.BlueToothScanHelper;
import com.example.wifidemo1.permission.PermissionUtil;

/**
 * @author: fuhejian
 * @date: 2023/3/6
 */
public class HomeActivityInitViewImpl implements InitView<ActivityMainBinding> {


    /**
     * 这里面可以去执行耗时任务
     *
     * @param binding
     */
    @Override
    public void initViewAsync(ActivityMainBinding binding, LifecycleOwner lifecycleOwner) {
        InitView.super.initViewAsync(binding, lifecycleOwner);
    }

    @Override
    public void initView(ActivityMainBinding binding, LifecycleOwner lifecycleOwner) {
        //初始化ViewPager2
        initViewPager2(binding);
        //
    }

    /**
     * 初始化ViewPager2
     */
    private void initViewPager2(ActivityMainBinding binding) {
        ViewPager2 viewPager2 = binding.fragmentContainer;
        BaseDataBindingFragmentAdapter adapter = null;
        FragmentActivity fragmentActivity = ContextUtil.INSTANCE.findAppCompatActivity(viewPager2.getContext());
        if(fragmentActivity!=null){
            adapter = new ViewPager2FragmentAdapter(fragmentActivity);
            viewPager2.setAdapter(adapter);
            //添加fragment
            adapter.addFragment(new HomeFragment());
        }
    }
}
