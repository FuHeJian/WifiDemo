package com.example.wifidemo1.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * registeractivityresult的activity基类
 *
 * @author: fuhejian
 * @date: 2023/3/7
 */
public abstract class BaseActivity extends AppCompatActivity {

    private ViewModelProvider mViewModelProvider = null;
    private ArraySet<RegisterForActivityResultListener> mRegisterForActivityResultListenerList = new ArraySet<>();
    private ActivityResultLauncher<Intent> mRegisterForActivityResult;

    /**
     * 由子类实现,用于创建ActivityResultLauncher
     * registerForActivityResult需要在activity create生命周期调用，否则会报错
     * 所以这里封装一下，以防报错。
     * @return ActivityResultLauncher
     */
    @NotNull
    abstract protected ActivityResultLauncher<Intent> createRegisterForActivityResult();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModelProvider = new ViewModelProvider(this);
        mRegisterForActivityResult = createRegisterForActivityResult();
    }

    public ActivityResultLauncher<Intent> getRegisterForActivityResult() {
        return mRegisterForActivityResult;
    }

    public ArraySet<RegisterForActivityResultListener> getRegisterForActivityResultListenerList() {
        return mRegisterForActivityResultListenerList;
    }

    public void addRegisterForActivityResultListener(RegisterForActivityResultListener listener) {
        if (listener != null) {
            mRegisterForActivityResultListenerList.add(listener);
        }
    }

    public void removeRegisterForActivityResultListener(RegisterForActivityResultListener listener) {
        if (listener != null) {
            mRegisterForActivityResultListenerList.remove(listener);
        }
    }

    public void clearRegisterForActivityResultListener() {
        mRegisterForActivityResultListenerList.clear();
    }

    public void dispatchRegisterForActivityResultListener(ActivityResult result){
        mRegisterForActivityResultListenerList.forEach((listener)->{
            listener.onResult(result);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearRegisterForActivityResultListener();
    }

    public <T extends ViewModel> T getViewModel(Class<T> cla){
        return mViewModelProvider.get(cla);
    }

    public static interface RegisterForActivityResultListener {

        void onResult(ActivityResult result);

    }

}
