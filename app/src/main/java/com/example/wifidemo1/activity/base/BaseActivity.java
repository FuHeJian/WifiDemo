package com.example.wifidemo1.activity.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * registeractivityresult的activity基类
 *
 * @author: fuhejian
 * @date: 2023/3/7
 */
public abstract class BaseActivity extends AppCompatActivity {

    static public final String REQUEST_PERMISSION_INTENT_ACTION = "androidx.activity.result.contract.action.REQUEST_PERMISSIONS";

    private ViewModelProvider mViewModelProvider = null;
    private ArraySet<RegisterForActivityResultListener> mRegisterForActivityResultListenerList = new ArraySet<>();
    private ActivityResultLauncher<Intent> mRegisterForActivityResult;
    private ActivityResultLauncher<String[]> mRegisterForPermissionsResult;
    private ArraySet<RegisterForPermissionsResultListener> mRegisterForPermissionsResultListenerList = new ArraySet<>();

    /**
     * 由子类实现,用于创建ActivityResultLauncher
     * registerForActivityResult需要在activity create生命周期调用，否则会报错
     * 所以这里封装一下，以防报错。
     * @return ActivityResultLauncher
     */
    @NotNull
    protected ActivityResultLauncher<Intent> createRegisterForActivityResult(){
        return registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::dispatchRegisterForActivityResultListener);
    }

    protected ActivityResultLauncher<String[]> createRegisterForPermissionsResult(){
        return registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this::dispatchRegisterForPermissionsResultListener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModelProvider = new ViewModelProvider(this);
        mRegisterForActivityResult = createRegisterForActivityResult();
        mRegisterForPermissionsResult = createRegisterForPermissionsResult();
    }

    public ActivityResultLauncher<Intent> getRegisterForActivityResult() {
       return mRegisterForActivityResult;
    }

    public ActivityResultLauncher<String[]> getRegisterForPermissionsResult() {
       return mRegisterForPermissionsResult;
    }

    public ArraySet<RegisterForActivityResultListener> getRegisterForActivityResultListenerList() {
        return mRegisterForActivityResultListenerList;
    }

    public void addRegisterForActivityResultListener(RegisterForActivityResultListener listener) {
        if (listener != null) {
            mRegisterForActivityResultListenerList.add(listener);
        }
    }

    public void addRegisterForPermissionsResultListener(RegisterForPermissionsResultListener listener) {
        if (listener != null) {
            mRegisterForPermissionsResultListenerList.add(listener);
        }
    }

    public void removeRegisterForPermissionsResultListener(RegisterForPermissionsResultListener listener) {
        if (listener != null) {
            mRegisterForPermissionsResultListenerList.remove(listener);
        }
    }
    public void dispatchRegisterForPermissionsResultListener(Map<String,Boolean> result){
        mRegisterForPermissionsResultListenerList.forEach((listener)->{
            listener.onResult(result);
        });
    }

    public void removeRegisterForActivityResultListener(RegisterForActivityResultListener listener) {
        if (listener != null) {
            mRegisterForActivityResultListenerList.remove(listener);
        }
    }

    public void clearRegisterForActivityResultListener() {
        mRegisterForActivityResultListenerList.clear();
    }

    //分发ActivityResult到通过addRegisterForActivityResultListener添加的Listener
    public void dispatchRegisterForActivityResultListener(ActivityResult result){
        mRegisterForActivityResultListenerList.forEach((listener)->{
            listener.onResult(result);
        });
    }

    public void dispatchRegisterForActivityPermissionsListener(ActivityResult result){
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

    public static interface RegisterForPermissionsResultListener {

        void onResult(Map<String, Boolean> result);

    }

}
