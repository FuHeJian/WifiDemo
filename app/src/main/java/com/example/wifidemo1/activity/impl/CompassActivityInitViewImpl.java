package com.example.wifidemo1.activity.impl;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleOwner;

import com.example.wifidemo1.Executors.ExecutorsUtil;
import com.example.wifidemo1.activity.base.BaseActivity;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.customview.MyProgressBar;
import com.example.wifidemo1.databinding.CompassMainBinding;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.permission.PermissionUtil;
import com.google.android.material.shape.MarkerEdgeTreatment;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.tooltip.TooltipDrawable;

import org.shredzone.commons.suncalc.MoonTimes;
import org.shredzone.commons.suncalc.SunTimes;

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

/*        ShapeAppearanceModel builder = ShapeAppearanceModel.builder().setRightEdge(new TriangleEdgeTreatment(30, false) {
            @Override
            public void getEdgePath(float length, float center, float interpolation, @NonNull ShapePath shapePath) {
                super.getEdgePath(length, 0, interpolation, shapePath);
            }
        }).setAllCorners(new RoundedCornerTreatment()).setAllCornerSizes(20f).build();*/

//        ShapeAppearanceModel builder = ShapeAppearanceModel.builder().setBottomEdge(new MarkerEdgeTreatment(30f)).build();

        ShapeAppearanceModel builder = ShapeAppearanceModel.builder().setBottomEdge(new MarkerEdgeTreatment(30f)).build();

        MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable(builder);
        materialShapeDrawable.setTint(Color.parseColor("#ff0000"));
        materialShapeDrawable.setPaintStyle(Paint.Style.FILL);

        binding.progressBar.setScrollListener(new MyProgressBar.ScrollListener() {
            @Override
            public void onMinPositionChanged(float value) {
                int _value = (int)(100 + (value * 900));
                MyLog.printLog("当前类:CompassActivityInitViewImpl,当前方法：onMinPositionChanged,当前线程:" + Thread.currentThread().getName() + ",信息:" + _value);
                TooltipDrawable minTooltip = binding.progressBar.getMinTooltip();
                minTooltip.setText(String.valueOf((int)_value));
//                minTooltip.setBounds(child.getLeft(), child.getTop() - minTooltip.getIntrinsicHeight(), child.getLeft() + tooltipDrawable.getIntrinsicWidth(), child.getTop());
            }

            @Override
            public void onMaxPositionChanged(float value) {
                MyLog.printLog("当前类:CompassActivityInitViewImpl,当前方法：onMaxPositionChanged,当前线程:" + Thread.currentThread().getName() + ",信息:" + (100 + (value * 900)));

                int _value = (int)(100 + (value * 900));
                MyLog.printLog("当前类:CompassActivityInitViewImpl,当前方法：onMinPositionChanged,当前线程:" + Thread.currentThread().getName() + ",信息:" + _value);
                TooltipDrawable minTooltip = binding.progressBar.getMaxTooltip();
                minTooltip.setText(String.valueOf((int)_value));
                View child2 = binding.progressBar.getChildAt(1);

//                maxTooltipDrawable.setBounds(child2.getLeft(), child2.getTop() - maxTooltipDrawable.getIntrinsicHeight(), child2.getLeft() + maxTooltipDrawable.getIntrinsicWidth(), child2.getTop());
            }

            @Override
            public void onDrawMinValue(Canvas canvas, float value) {
                binding.progressBar.getMinTooltip().draw(canvas);
            }

            @Override
            public void onDrawMaxValue(Canvas canvas, float value) {
                binding.progressBar.getMaxTooltip().draw(canvas);
            }
        });



/*        ((ViewGroup)binding.text.getParent()).setClipChildren(false);

        binding.text.setBackground(materialShapeDrawable);*/

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
