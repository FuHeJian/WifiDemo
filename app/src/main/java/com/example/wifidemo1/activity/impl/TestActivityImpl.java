package com.example.wifidemo1.activity.impl;

import android.graphics.Color;

import androidx.lifecycle.LifecycleOwner;

import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.customview.BasePickVerticalScrollBar;
import com.example.wifidemo1.databinding.TestBinding;

import java.util.ArrayList;

/**
 * @author: fuhejian
 * @date: 2023/5/31
 */
public class TestActivityImpl implements InitView<TestBinding> {

    @Override
    public void initView(TestBinding binding, LifecycleOwner lifecycleOwner) {
        /*binding.content.setSliderNum(4);
        ;
        binding.content.setColorList(Color.argb(0.6f, 0, 0, 1), Color.argb(0.6f, 0, 1, 0));

        ArrayList<String> datas = new ArrayList<>();

        ArrayList<Float> values = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String s = String.valueOf(i);
            datas.add(s);
            values.add(Float.parseFloat(s));
        }

        //view.post是在测量之后才执行的（如果view还没有开始测量，则会在测量开始的时候在加入到looper messageQueen的队列中,否则直接加入）
        binding.content.post(
                new Runnable() {
                    @Override
                    public void run() {
                        binding.content.setDataList(datas, values);
                        binding.content.setSlidersValue(49, 49, 49, 49);
                    }
                }
        );

        binding.content.setSelectListener(new BasePickVerticalScrollBar.SelectListener() {
            @Override
            public void onValueSelect(BasePickVerticalScrollBar.Slider slider) {
                binding.title.setText(slider.tickMark.rawValue);
            }
        });*/


    }

}
