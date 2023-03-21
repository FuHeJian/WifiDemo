package com.example.wifidemo1.activity;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.example.wifidemo1.activity.base.BaseDataBindingActivity;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.activity.impl.SecondActivityInitViewImpl;
import com.example.wifidemo1.databinding.SecondActivityBinding;

/**
 * @author: fuhejian
 * @date: 2023/3/17
 */
public class SecondActivity extends BaseDataBindingActivity {
    @NonNull
    @Override
    protected ActivityResultLauncher<Intent> createRegisterForActivityResult() {
        return null;
    }

    @NonNull
    @Override
    public ViewDataBinding createDataBinding() {
        return SecondActivityBinding.inflate(getLayoutInflater());
    }

    @NonNull
    @Override
    public InitView<SecondActivityBinding> createIntiView() {
        return new SecondActivityInitViewImpl();
    }

    @Override
    public Class getViewModel() {
        return null;
    }
}
