package com.example.wifidemo1.fragment.i;

import androidx.lifecycle.LifecycleOwner;

/**
 * 用于初始化view的接口
 * @author: fuhejian
 * @date: 2023/3/6
 */
public interface FragmentInitView<T> {
    /**
     * 主线程执行
     */
    void initView(T binding, LifecycleOwner lifecycleOwner);

}
