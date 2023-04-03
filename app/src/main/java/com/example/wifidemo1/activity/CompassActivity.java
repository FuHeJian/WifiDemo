package com.example.wifidemo1.activity;

import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    protected ActivityResultLauncher<Intent> createRegisterForActivityResult() {
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::dispatchRegisterForActivityResultListener);
    }

    @NonNull
    @Override
    public CompassMainBinding createDataBinding() {
        return CompassMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected ActivityResultLauncher<String[]> createRegisterForPermissionsResult() {
        return registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this::dispatchRegisterForPermissionsResultListener);
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
