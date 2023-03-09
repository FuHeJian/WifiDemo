package com.example.wifidemo1.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.example.wifidemo1.databinding.HomeFragmentBinding;
import com.example.wifidemo1.fragment.base.BaseDataBindingFragment;
import com.example.wifidemo1.fragment.i.FragmentInitView;
import com.example.wifidemo1.fragment.impl.HomeFragmentInitViewImpl;
import com.example.wifidemo1.viewmodel.HomeViewModel;

/**
 * @author: fuhejian
 * @date: 2023/3/8
 */
public class HomeFragment extends BaseDataBindingFragment<HomeFragmentBinding> {

    @Override
    public <M extends ViewModel> Class<M> getViewModel() {
        return (Class<M>) HomeViewModel.class;
    }

    @NonNull
    @Override
    public HomeFragmentBinding createDataBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return HomeFragmentBinding.inflate(inflater);
    }

    @Override
    public FragmentInitView<HomeFragmentBinding> createIntiView() {
        return new HomeFragmentInitViewImpl();
    }

}
