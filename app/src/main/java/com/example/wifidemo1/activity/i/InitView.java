package com.example.wifidemo1.activity.i;

import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;

import io.reactivex.rxjava3.core.Flowable;

/**
 * 用于初始化view的接口
 * @author: fuhejian
 * @date: 2023/3/6
 */
public interface InitView<T> {
    /**
     * 主线程执行
     */
    void initView(T binding, LifecycleOwner lifecycleOwner);

    /**
     * 异步执行,
     * 子类实现这个方法，可以在子线程中初始化view,
     * 也可以不实现,
     * 这个函数后于{@link #initView(T)}执行
     *
     */
    public default void initViewAsync(T binding, LifecycleOwner lifecycleOwner){
    }
}
