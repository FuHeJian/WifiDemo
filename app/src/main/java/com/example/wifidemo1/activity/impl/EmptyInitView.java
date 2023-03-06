package com.example.wifidemo1.activity.impl;


import com.example.wifidemo1.activity.i.InitView;

/**
 * 防止空异常报错，默认提供一个空实现
 * @author: fuhejian
 * @date: 2023/3/6
 */
public class EmptyInitView<T> implements InitView<T> {

    @Override
    public void initView(T binding) {

    }
}
