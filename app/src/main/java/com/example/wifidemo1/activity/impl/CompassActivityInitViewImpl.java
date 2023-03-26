package com.example.wifidemo1.activity.impl;

import android.hardware.Sensor;

import androidx.lifecycle.LifecycleOwner;

import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.customview.Compass;
import com.example.wifidemo1.databinding.CompassMainBinding;

/**
 * com.example.wifidemo1.activity.impl
 */
public class CompassActivityInitViewImpl implements InitView<CompassMainBinding> {
    @Override
    public void initView(CompassMainBinding binding, LifecycleOwner lifecycleOwner) {
            binding.compass.setListener(new Compass.Listener() {
                @Override
                public void onPositionChanged(int value) {

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    switch (accuracy){
                        case 3:{
                            binding.hint.setText("准确度正常");
                            break;
                        }
                        default:{
                            binding.hint.setText("受磁场干扰，准确度降低");
                        }
                    }
                }
            });
    }
}
