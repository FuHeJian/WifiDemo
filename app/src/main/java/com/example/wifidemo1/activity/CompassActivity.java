package com.example.wifidemo1.activity;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.wifidemo1.activity.base.BaseDataBindingActivity;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.activity.impl.CompassActivityInitViewImpl;
import com.example.wifidemo1.databinding.CompassMainBinding;

/**
 * com.example.wifidemo1.activity
 */
public class CompassActivity extends BaseDataBindingActivity<CompassMainBinding> {

    @NonNull
    @Override
    public CompassMainBinding createDataBinding() {
        return CompassMainBinding.inflate(getLayoutInflater());
    }

    @SuppressLint({"MissingPermission", "NewApi"})
    @NonNull
    @Override
    public InitView<CompassMainBinding> createIntiView() {
        return new CompassActivityInitViewImpl();
    }

    @Override
    public <M extends ViewModel> Class<M> getViewModel() {
        return null;
    }


}
