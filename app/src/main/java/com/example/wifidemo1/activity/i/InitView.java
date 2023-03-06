package com.example.wifidemo1.activity.i;

import androidx.databinding.ViewDataBinding;

/**
 * 用于初始化view的接口
 * @author: fuhejian
 * @date: 2023/3/6
 */
public interface InitView<T> {
    /**
     * 主线程执行
     */
    public void initView(T binding);

    /**
     * 异步执行,
     * 子类实现这个方法，可以在子线程中初始化view,
     * 也可以不实现,
     * 这个函数先于initView执行
     */
    public default void initViewAsync(T binding){

    }
}
