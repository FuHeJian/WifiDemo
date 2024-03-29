package com.example.wifidemo1.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.example.wifidemo1.databinding.CameraBinding;
import com.example.wifidemo1.fragment.base.BaseDataBindingFragment;
import com.example.wifidemo1.fragment.i.FragmentInitView;
import com.example.wifidemo1.fragment.impl.CameraFragmentImpl;

/**
 * @author: fuhejian
 * @date: 2023/3/9
 */
public class CameraFragment extends BaseDataBindingFragment<CameraBinding>{
    @Override
    public <M extends ViewModel> Class<M> getViewModel() {
        return null;
    }

    @NonNull
    @Override
    public CameraBinding createDataBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return CameraBinding.inflate(inflater);
    }

    @NonNull
    @Override
    public FragmentInitView<CameraBinding> createIntiView() {
        return new CameraFragmentImpl();
    }
}
