package com.example.wifidemo1.fragment.impl;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;

import com.example.wifidemo1.fragment.i.FragmentInitView;

/**
 * @author: fuhejian
 * @date: 2023/3/8
 */
public class EmptyFragmentInitView<T extends ViewDataBinding> implements FragmentInitView<T> {

    @Override
    public void initView(T binding, LifecycleOwner lifecycleOwner) {

    }
}
