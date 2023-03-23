package com.example.wifidemo1.activity.impl;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.wifidemo1.activity.ContextUtil;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.adapter.BaseDataBindingFragmentAdapter;
import com.example.wifidemo1.adapter.ViewPager2FragmentAdapter;
import com.example.wifidemo1.customview.IndicatorMediator;
import com.example.wifidemo1.databinding.ActivityMainBinding;
import com.example.wifidemo1.fragment.CameraFragment;
import com.example.wifidemo1.fragment.HomeFragment;
import com.example.wifidemo1.fragment.TestFragment;

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
            adapter.addFragment(new CameraFragment());
            adapter.addFragment(new TestFragment());
            //设置一屏多页
            ((RecyclerView) viewPager2.getChildAt(0)).setPadding(0,0,300,0);
            ((RecyclerView) viewPager2.getChildAt(0)).setClipToPadding(false);
            viewPager2.setOffscreenPageLimit(adapter.getItemCount()-1);

            IndicatorMediator indicatorMediator = new IndicatorMediator(binding.indicator,binding.fragmentContainer);
            indicatorMediator.attach();
        }

    }
}