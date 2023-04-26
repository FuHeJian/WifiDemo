package com.example.wifidemo1.activity.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;

import com.example.wifidemo1.activity.i.InitView;
import com.example.wifidemo1.activity.impl.EmptyInitView;
import com.example.wifidemo1.activity.theme.ThemeUtil;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author: fuhejian
 * @date: 2023/3/6
 */
public abstract class BaseDataBindingActivity<T extends ViewDataBinding> extends BaseActivity {

    /**
     * 保存该activity的ViewDataBinding
     */
    private T mDataBinding;
    /**
     * 异步worker,类似线程池
     */
    private Scheduler.Worker mWorker = Schedulers.io().createWorker();

    /**
     * createIntiView()返回的对象
     */
    private InitView<T> mInitViewCallback = new EmptyInitView<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建dataBinding
        mDataBinding = createDataBinding();
        mDataBinding.setLifecycleOwner(this);
        ViewModel viewModel = null;
        Class<ViewModel> viewModelClass = getViewModel();

        //设置全屏模式
        ThemeUtil.INSTANCE.setSystemStatusBar(this,true,true);

        if (viewModelClass != null) {
            viewModel = getViewModel(getViewModel());
            int id = getRestoreViewModelBRId();
            if(id != -1){
                mDataBinding.setVariable(id, viewModel);
            }
        }

        setContentView(mDataBinding.getRoot());

        mInitViewCallback = createIntiView();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //初始化view,放在start的目的是，fragment的view 创建会晚于activity的onCreate，
        //如果initView放在onCreate初始化可能会失败
        //而fragment的view初始化正是在onStart之后
        //主线程初始化
        mInitViewCallback.initView(mDataBinding, this);

        //异步初始化
        mWorker.schedule(() -> {//runnable 的 lambda
            mInitViewCallback.initViewAsync(mDataBinding, this);
            mWorker.dispose();
        });

    }


    /**
     * 恢复ViewModel 返回BR.id
     *
     * @return
     */
    public int getRestoreViewModelBRId() {
        return -1;
    }

    /**
     * 生成dataBinding,
     * 需要子类实现，并返回自己的dataBinding
     *
     * @return {@link T}
     */
    @NotNull
    abstract public T createDataBinding();

    /**
     * 创建用于初始化View的InitView，
     * 用于调用InitView.initView()方法
     *
     * @return {@link InitView}
     */
    @NotNull
    abstract public InitView<T> createIntiView();


    /**
     * 保存数据，防止横竖屏切换时重新获取数据
     * 恢复数据需要配合dataBinding使用
     * {@code
     * <data>
     * <variable
     * name="viewModel"
     * type="com.example.wifidemo1.viewmodel.HomeViewModel" />
     * </data>
     * }
     *
     * @return
     */
    abstract public <M extends ViewModel> Class<M> getViewModel();

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放线程资源
        if (!mWorker.isDisposed()) {
            mWorker.dispose();
        }
        mWorker = null;
    }
}
