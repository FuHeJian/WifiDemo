package com.example.wifidemo1.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.example.wifidemo1.databinding.TestFragmentBinding;
import com.example.wifidemo1.fragment.base.BaseDataBindingFragment;
import com.example.wifidemo1.fragment.i.FragmentInitView;
import com.example.wifidemo1.fragment.impl.TestFragmentInitViewImpl;

/**
 * @author: fuhejian
 * @date: 2023/3/21
 */
public class TestFragment extends BaseDataBindingFragment<TestFragmentBinding> {

    @Override
    public <M extends ViewModel> Class<M> getViewModel() {
        return null;
    }

    @NonNull
    @Override
    public TestFragmentBinding createDataBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return TestFragmentBinding.inflate(getLayoutInflater());
    }

    @NonNull
    @Override
    public FragmentInitView<TestFragmentBinding> createIntiView() {
        return new TestFragmentInitViewImpl();
    }
}
