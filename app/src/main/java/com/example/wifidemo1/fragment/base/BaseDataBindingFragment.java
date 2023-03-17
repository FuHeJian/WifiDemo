package com.example.wifidemo1.fragment.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.wifidemo1.BR;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.activity.impl.EmptyInitView;
import com.example.wifidemo1.fragment.i.FragmentInitView;
import com.example.wifidemo1.fragment.impl.EmptyFragmentInitView;

import org.jetbrains.annotations.NotNull;

/**
 * @author: fuhejian
 * @date: 2023/3/8
 */
public abstract class BaseDataBindingFragment<T extends ViewDataBinding> extends Fragment {

    private T mDataBinding;
    private FragmentInitView<T> mInitViewCallback = new EmptyFragmentInitView<>();

    private ViewModelProvider mViewModelProvider = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModelProvider = new ViewModelProvider(this);

    }

    public <T extends ViewModel> T getViewModel(Class<T> cla){
        return mViewModelProvider.get(cla);
    }

    abstract public <M extends ViewModel> Class<M> getViewModel();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDataBinding = createDataBinding(inflater, container, savedInstanceState);
        mDataBinding.setLifecycleOwner(this);
        //viewModel 数据恢复或初始化 start
        ViewModel viewModel = null;
        Class<ViewModel> viewModelClass = getViewModel();
        if (viewModelClass != null) {
            viewModel = getViewModel(getViewModel());
            mDataBinding.setVariable(BR.viewModel, viewModel);
        }
        //viewModel 数据恢复或初始化 end

        //初始化view
        mInitViewCallback = createIntiView();
        //主线程初始化
        mInitViewCallback.initView(mDataBinding,getViewLifecycleOwner());

        return mDataBinding.getRoot();
    }

    @NotNull
    public abstract T createDataBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    @NotNull
    abstract public FragmentInitView<T> createIntiView();

}
