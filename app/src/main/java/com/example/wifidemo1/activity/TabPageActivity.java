package com.example.wifidemo1.activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.wifidemo1.activity.base.BaseDataBindingActivity;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.activity.impl.TabPageActivityImpl;
import com.example.wifidemo1.databinding.TabPageBinding;

/**
 * @author: fuhejian
 * @date: 2023/5/30
 */
public class TabPageActivity extends BaseDataBindingActivity<TabPageBinding> {

    @NonNull
    @Override
    public TabPageBinding createDataBinding() {
        return TabPageBinding.inflate(getLayoutInflater());
    }

    @NonNull
    @Override
    public InitView<TabPageBinding> createIntiView() {
        return new TabPageActivityImpl();
    }

    @Override
    public <M extends ViewModel> Class<M> getViewModel() {
        return null;
    }


}
