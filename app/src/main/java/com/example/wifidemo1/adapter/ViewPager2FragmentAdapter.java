package com.example.wifidemo1.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

/**
 * @author: fuhejian
 * @date: 2023/3/8
 */
public class ViewPager2FragmentAdapter extends BaseDataBindingFragmentAdapter{

    public ViewPager2FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        this(fragmentActivity.getSupportFragmentManager(), fragmentActivity.getLifecycle());
    }

    public ViewPager2FragmentAdapter(@NonNull Fragment fragment) {
        this(fragment.getChildFragmentManager(), fragment.getLifecycle());
    }

    public ViewPager2FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }



}
