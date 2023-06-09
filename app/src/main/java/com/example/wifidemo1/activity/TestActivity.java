package com.example.wifidemo1.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.wifidemo1.activity.base.BaseDataBindingActivity;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.activity.impl.TestActivityImpl;
import com.example.wifidemo1.databinding.TestBinding;

/**
 * @author: fuhejian
 * @date: 2023/5/31
 */
public class TestActivity extends BaseDataBindingActivity<TestBinding> {

    @NonNull
    @Override
    public TestBinding createDataBinding() {
        return TestBinding.inflate(getLayoutInflater());
    }

    @NonNull
    @Override
    public InitView<TestBinding> createIntiView() {
        return new TestActivityImpl();
    }

    @Override
    public <M extends ViewModel> Class<M> getViewModel() {
        return null;
    }
}
