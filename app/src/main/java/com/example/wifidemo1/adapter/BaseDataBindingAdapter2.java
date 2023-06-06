package com.example.wifidemo1.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.AsyncDifferConfig;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifidemo1.log.MyLog;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * RecyclerView ListAdapter的基类
 * 不需要立即更新UI的话使用addItem代替submitList
 *
 * @param <T>
 */
public abstract class BaseDataBindingAdapter2<T> extends ListAdapter<T, RecyclerView.ViewHolder> {

    private LifecycleOwner mLifecycleOwner;

    protected ArrayList<T> mCacheList = new ArrayList<>();
    private int mOldCacheListSize = 0;

    private Disposable timerDisposable;

    private boolean needStopTimer = false;

    public BaseDataBindingAdapter2(@NonNull DiffUtil.ItemCallback<T> diffCallback, @NotNull LifecycleOwner lifecycleOwner) {
        super(diffCallback);
        mLifecycleOwner = lifecycleOwner;
    }

    public BaseDataBindingAdapter2(@NonNull AsyncDifferConfig<T> config, @NotNull LifecycleOwner lifecycleOwner) {
        super(config);
        mLifecycleOwner = lifecycleOwner;
    }

    class VH extends RecyclerView.ViewHolder {
        public VH(@NonNull View itemView) {
            super(itemView);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getLayoutId(), parent, false);

        dataBinding.setLifecycleOwner(mLifecycleOwner);

        return new VH(dataBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewDataBinding binding = DataBindingUtil.getBinding(holder.itemView);
        if (binding != null) {
            onBindItem(binding, getItem(position), position);
        }

    }

    @SuppressLint("MissingPermission")
    public void addItem(T item) {
        if (item != null) {
            mCacheList.add(item);
        }
        startTimer();
    }

    @SuppressLint("MissingPermission")
    public void removeItem(T item) {
        if (item != null) {
            mCacheList.remove(item);
        }
        startTimer();
    }

    @SuppressLint("MissingPermission")
    public void removeItem(int index) {
        if (index >= 0 && index < mCacheList.size()) {
            mCacheList.remove(index);
        }
        startTimer();
    }

    /**
     * 停止后台对recyclerview数据不断的更新
     * 这个一定要调用
     */
    public void stopTimer() {
        needStopTimer = true;
        if (timerDisposable != null) {
            timerDisposable.dispose();
            timerDisposable = null;
        }
    }

    public void startTimer() {
        needStopTimer = false;
        if (timerDisposable == null) {
            timerDisposable = AndroidSchedulers.mainThread().schedulePeriodicallyDirect(new Runnable() {
                private int num = 0;

                @Override
                public void run() {
                    if (mOldCacheListSize != getCurrentList().size()) {
                        MyLog.printLog("当前类:BaseDataBindingAdapter,信息:" + "submit还没有执行完");
                    } else {
                        if (mOldCacheListSize == mCacheList.size()) {
                            num++;
                            if (num > 10) stopTimer();
                            MyLog.printLog("当前类:BaseDataBindingAdapter,信息:" + "submit执行完了，但还没有新增内容");
                        } else {
                            num = 0;
                            ArrayList<T> _list = (ArrayList<T>) mCacheList.clone();
                            BaseDataBindingAdapter2.this.submitList(_list);
                            mOldCacheListSize = _list.size();
                            MyLog.printLog("当前类:BaseDataBindingAdapter,信息:" + "submit执行完了，缓存中的大小" + _list.size());
                        }

                        if (needStopTimer) {
                            MyLog.printLog("当前类:BaseDataBindingAdapter,信息:" + "已销毁，总共获取到的设备：" + mCacheList.size());
//                            mCacheList.clear();
                        }
                    }
                }
            }, 1000L, 2000L, TimeUnit.MILLISECONDS);
        }
    }

    abstract public void onBindItem(ViewDataBinding binding, T item, int position);

    abstract public @LayoutRes int getLayoutId();

}
