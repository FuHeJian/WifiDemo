package com.example.wifidemo1.activity.impl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wifidemo1.Executors.ExecutorsUtil;
import com.example.wifidemo1.activity.base.BaseActivity;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.activity.impl.adapter.TestAdapter;
import com.example.wifidemo1.databinding.CompassMainBinding;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.permission.PermissionUtil;

import org.shredzone.commons.suncalc.MoonTimes;
import org.shredzone.commons.suncalc.SunTimes;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

/**
 * com.example.wifidemo1.activity.impl
 */
public class CompassActivityInitViewImpl implements InitView<CompassMainBinding> {

    private boolean isCompute = false;


    @SuppressLint("RestrictedApi")
    @Override
    public void initView(CompassMainBinding binding, LifecycleOwner lifecycleOwner) {

        /*binding.content.setSliderNum(4);

        ArrayList<String> datas = new ArrayList<>();

        for (int i = 0; i < 30; i++) {
            datas.add(String.valueOf(i));
        }

        //view.post是在测量之后才执行的（如果view还没有开始测量，则会在测量开始的时候在加入到looper messageQueen的队列中,否则直接加入）
        binding.content.post(
                new Runnable() {
                    @Override
                    public void run() {
                        binding.content.setDataList(datas);
                        binding.content.setSlidersValue(2, 8, 11);
                    }
                }
        );*/


        binding.recy.setLayoutManager(new LinearLayoutManager(binding.recy.getContext(),LinearLayoutManager.VERTICAL,false));

        TestAdapter adapter = new TestAdapter(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }
        },lifecycleOwner);

        binding.recy.setAdapter(adapter);

        ArrayList<String> strings = new ArrayList<>();
        strings.add("hello1");
        strings.add("hello2");
        strings.add("hello3");
        strings.add("hello3");
        strings.add("hello3");
        strings.add("hello3");
        strings.add("hello3");
        strings.add("hello3");
        adapter.submitList(strings);


    }

    /**
     * 根据经纬度获取日出日落月出月落时间
     *
     * @param binding
     * @param activity
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    @SuppressLint("MissingPermission")
    private void dispatchLocation(CompassMainBinding binding, BaseActivity activity) {

        final double[] latitude = {0};
        final double[] longitude = {0};

        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationManager.getAllProviders().forEach(it -> {
            if (locationManager.isProviderEnabled(it)) {
                locationManager.getCurrentLocation(it, null, ExecutorsUtil.IO, new Consumer<Location>() {

                    @Override
                    public void accept(Location location) {

                        if (isCompute) return;

                        latitude[0] = location.getLatitude();
                        longitude[0] = location.getLongitude();
                        MyLog.printLog("当前类:CompassActivity,当前方法：accept,当前线程:" + Thread.currentThread().getName() + ",信息:" + it + ",当前经度：" + location.getLatitude() + ",经度：" + location.getLongitude());

                        long v9 = System.currentTimeMillis() - 86400000L;

                        //计算太阳，月亮出现与消失时间
                        if (latitude[0] != 0 && longitude[0] != 0) {

                            SunTimes suntimes = SunTimes.compute()
                                    .at(latitude[0], longitude[0])   // set a location
                                    .execute();

                            suntimes.getRise();//日出
                            suntimes.getSet();//日落

                            MoonTimes moonTimes = MoonTimes.compute()
                                    .at(latitude[0], longitude[0])
                                    .execute();

                            moonTimes.getRise();//月出
                            moonTimes.getSet();//月落

                            isCompute = true;//只获取一次计算数据

                        }
                    }
                });
            }
        });

    }

    /**
     * 获取经纬度，并调用dispatchLocation
     *
     * @param binding
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void getLocation(CompassMainBinding binding) {
        //获取经纬度
        BaseActivity thisActivity = (BaseActivity) binding.getRoot().getContext();
        if (PermissionUtil.checkLocation(thisActivity)) {
            dispatchLocation(binding, thisActivity);
        } else {
            thisActivity.addRegisterForPermissionsResultListener(new BaseActivity.RegisterForPermissionsResultListener() {
                int re = 0;

                @Override
                public void onResult(Map<String, Boolean> result) {
                    result.forEach((p, r) -> {
                        if (p.equals(Manifest.permission.ACCESS_COARSE_LOCATION) && r) {
                            re++;
                        }
                        if (p.equals(Manifest.permission.ACCESS_FINE_LOCATION) && r) {
                            re++;
                        }
                    });
                    if (re == 2) {
                        dispatchLocation(binding, thisActivity);
                    }
                    thisActivity.removeRegisterForPermissionsResultListener(this);
                }

            });
            Intent intent = new Intent();
            intent.setAction(thisActivity.REQUEST_PERMISSION_INTENT_ACTION);
            thisActivity.getRegisterForPermissionsResult().launch(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

}
