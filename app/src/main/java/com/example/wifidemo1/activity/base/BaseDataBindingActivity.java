package com.example.wifidemo1.activity.base;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.activity.impl.EmptyInitView;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author: fuhejian
 * @date: 2023/3/6
 */
public abstract class BaseDataBindingActivity<T extends ViewDataBinding> extends AppCompatActivity {

    /**
     * 保存该activity的ViewDataBinding
     */
    private  T mDataBinding;
    /**
     * 异步worker,类似线程池
     */
    private final Scheduler.Worker mWorker = Schedulers.io().createWorker();

    /**
     * createIntiView()返回的对象
     */
    private InitView<T> mInitViewCallback = new EmptyInitView<>();

    /**
     * 返回layoutId 用于创建view
     * @return layoutId
     */
    @LayoutRes
    abstract public int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建dataBinding
        mDataBinding = createDataBinding();
        //inflate view
        DataBindingUtil.setContentView(this,getLayoutId());
        //初始化view
        mInitViewCallback = createIntiView();
        //异步初始化
        mWorker.schedule(() -> {//runnable 的 lambda
            mInitViewCallback.initViewAsync(mDataBinding);
            mWorker.dispose();
        });
        //主线程初始化
        mInitViewCallback.initView(mDataBinding);
    }

    /**
     * 生成dataBinding,
     * 需要子类实现，并返回自己的dataBinding
     * @return {@link T}
     */
    @NotNull
    abstract public T createDataBinding();

    /**
     * 创建用于初始化View的InitView，
     * 用于调用InitView.initView()方法
     * @return {@link InitView}
     */
    @NotNull
    abstract public InitView<T> createIntiView();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!mWorker.isDisposed()) {
            mWorker.dispose();
        }
    }

}
