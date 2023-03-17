package com.example.wifidemo1.fragment.impl;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.net.IpSecManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.wifidemo1.App;
import com.example.wifidemo1.R;
import com.example.wifidemo1.activity.HomeActivity;
import com.example.wifidemo1.activity.PolarisUtil;
import com.example.wifidemo1.adapter.DevicesListAdapter;
import com.example.wifidemo1.customview.MyTimer;
import com.example.wifidemo1.databinding.BottomUpgradeHintBinding;
import com.example.wifidemo1.databinding.HomeFragmentBinding;
import com.example.wifidemo1.fragment.i.FragmentInitView;
import com.example.wifidemo1.helper.BlueToothScanHelper;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.network.NetWorkUtil;
import com.example.wifidemo1.network.PolarisSettings;
import com.example.wifidemo1.network.retrofit.PolarisNetWork;
import com.example.wifidemo1.permission.PermissionUtil;
import com.example.wifidemo1.utils.FileUtil;
import com.example.wifidemo1.utils.OrderCommunication;
import com.example.wifidemo1.utils.UpgradeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.Nullable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Cancellable;

/**
 * @author: fuhejian
 * @date: 2023/3/8
 */
public class HomeFragmentInitViewImpl implements FragmentInitView<HomeFragmentBinding> {
    private boolean canClick = false;

    @Override
    public void initView(HomeFragmentBinding binding, LifecycleOwner lifecycleOwner) {
        //将之后的socket都通过这个network进行连接，若通过network.bindSocket(socket),可以使该socket通过指定的network访问，不受bindProcessToNetwork影响。
        //connectivityManager.bindProcessToNetwork(network);
        binding.DevicesList.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext(), LinearLayoutManager.VERTICAL, false));
        DevicesListAdapter adapter;
        //横竖屏切换，保存数据
        if (binding.getViewModel() != null && binding.getViewModel().devicesListAdapter != null) {
            binding.setViewModel(binding.getViewModel());
            binding.DevicesList.setAdapter(binding.getViewModel().devicesListAdapter);
            adapter = binding.getViewModel().devicesListAdapter;
        } else {
            adapter = new DevicesListAdapter(new DiffUtil.ItemCallback<BluetoothDevice>() {
                @SuppressLint("MissingPermission")
                public boolean areItemsTheSame(@NonNull BluetoothDevice oldItem, @NonNull BluetoothDevice newItem) {
                    if (!PermissionUtil.checkBlueToothCONNECT(binding.getRoot().getContext())) {
                        return true;
                    }
                    if (oldItem.getName() != null && newItem.getName() != null) {
                        try {
                            //BluetoothDevice此时可能被释放导致异常
                            return oldItem.getName().equals(newItem.getName());
                        } catch (Exception e) {
                            return true;
                        }
                    }
                    return true;
                }

                @SuppressLint("MissingPermission")
                public boolean areContentsTheSame(@NonNull BluetoothDevice oldItem, @NonNull BluetoothDevice newItem) {
                    if (!PermissionUtil.checkBlueToothCONNECT(binding.getRoot().getContext())) {
                        return true;
                    }
                    if (oldItem.getName() != null && newItem.getName() != null) {
                        try {
                            //BluetoothDevice此时可能被释放导致异常
                            return oldItem.getName().equals(newItem.getName());
                        } catch (Exception e) {
                            return true;
                        }
                    }
                    return true;
                }
            }, lifecycleOwner);
            binding.DevicesList.setAdapter(adapter);
            if (binding.getViewModel() != null) {
                binding.getViewModel().devicesListAdapter = adapter;
            }

            //注册寻找polaris_2d3b07的该广播
            String bleStartName = "polaris";
            PolarisUtil.INSTANCE.registerBlueTooth((HomeActivity) binding.getRoot().getContext(), adapter, bleStartName, bluetoothAdapter -> {
                adapter.stopTimer();//必须调用
                canClick = true;
                Toast.makeText(binding.getRoot().getContext(), "蓝牙扫描结束！", Toast.LENGTH_SHORT).show();
            });
        }

        binding.loadBLE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (canClick) {
                    canClick = false;
                    Toast.makeText(v.getContext(), "开始扫描", Toast.LENGTH_SHORT).show();
                    canClick = false;
                    BlueToothScanHelper.scanBLE(v.getContext(), bluetoothAdapter -> {
                        canClick = true;
                        adapter.stopTimer();
                        Toast.makeText(binding.getRoot().getContext(), "蓝牙扫描结束！", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

    }

    public static class HomeFragmentHandler extends Handler {

        public static HomeFragmentHandler INSTANCE;

        static {
            INSTANCE = new HomeFragmentHandler();
        }

        private HomeFragmentHandler() {
            super(Looper.getMainLooper());
        }

        private HomeFragmentHandler(@NonNull Looper looper) {
            super(looper);
        }

        public static final int WHAT_UPGRADE_DIALOG = 0x1;

        /**
         * 非主动发送消息，勿调用
         */
        public static final int WHAT_UPGRADE_START = 0x2;

        public WeakReference<BottomSheetDialog> mBottomSheetDialog;

        public BottomUpgradeHintBinding mBottomUpgradeHintBinding;

        private void showDialog() {
            if (mBottomSheetDialog == null || mBottomSheetDialog.get() == null) {
                Context context = App.GlobalManager.INSTANCE.getContext("HomeActivity");
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                BottomUpgradeHintBinding bottomUpgradeHintBinding = BottomUpgradeHintBinding.inflate(bottomSheetDialog.getLayoutInflater());
                ViewDataBinding binding = DataBindingUtil.getBinding(bottomUpgradeHintBinding.getRoot());
                if (binding != null && context instanceof LifecycleOwner) {
                    binding.setLifecycleOwner((LifecycleOwner) context);
                }
                mBottomUpgradeHintBinding = bottomUpgradeHintBinding;
                bottomUpgradeHintBinding.upgradeRomVersionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (PolarisSettings.SoftWarePolarisRomVersion != null && PolarisSettings.SoftWarePolarisRomVersion.equals(PolarisSettings.LatestSoftWarePolarisRomVersion)) {
                            Toast.makeText(App.GlobalManager.INSTANCE.getContext(null), "当前已是最新版本", Toast.LENGTH_SHORT).show();
                        } else if (PolarisSettings.SoftWarePolarisRomVersion == null) {
                            OrderCommunication.getInstance().SP_GET_DEVICE_VERSION();
                        } else if (!PolarisSettings.uploading) {
                            OrderCommunication.getInstance().SP_SET_UPGRADE_START();//开始升级
                        }
                    }
//                        //TODO test
//                        OrderCommunication.getInstance().SP_SET_UPGRADE_START();//开始升级
                }
            );
            bottomUpgradeHintBinding.currentRomVersionText.setText(PolarisSettings.SoftWarePolarisRomVersion);
            bottomUpgradeHintBinding.latestRomVersionText.setText(PolarisSettings.LatestSoftWarePolarisRomVersion);
            bottomSheetDialog.setContentView(bottomUpgradeHintBinding.getRoot());
            bottomSheetDialog.show();
            mBottomSheetDialog = new WeakReference<>(bottomSheetDialog);
            if (PolarisSettings.SoftWarePolarisRomVersion.equals(PolarisSettings.LatestSoftWarePolarisRomVersion)) {
                Toast.makeText(App.GlobalManager.INSTANCE.getContext(null), "当前已是最新版本", Toast.LENGTH_SHORT).show();
            }
        } else

        {
            mBottomSheetDialog.get().show();
            mBottomUpgradeHintBinding.currentRomVersionText.setText(PolarisSettings.SoftWarePolarisRomVersion);
            mBottomUpgradeHintBinding.latestRomVersionText.setText(PolarisSettings.LatestSoftWarePolarisRomVersion);
        }
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        MyLog.printLog("当前类:HomeFragmentHandler,当前方法：handleMessage,信息:收到消息" + msg.what);
        switch (msg.what) {
            case WHAT_UPGRADE_DIALOG: {
                showDialog();
                break;
            }
            case WHAT_UPGRADE_START: {//不要主动发送这个消息，由CMD.SP_SET_UPGRADE_START返回成功的消息后被动调用
                showDialog();
                File updateFile = new File(PolarisSettings.DownloadedFileNamePath);
                //去升级
                PolarisSettings.uploading = true;
                int arg = msg.arg1;
                MyTimer.INSTANCE.singleSchedule(0, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //不要使用msg的arg参数，因为这里另开了一个线程，可能在message回收之后执行，导致arg参数失效
                            //解决办法，在线程创建前通过int arg = msg.arg1临时保存
                            switch (arg) {
                                case 0: {
                                    UpgradeUtils.INSTANCE.newUploadFile(new ObservableEmitter<Integer>() {

                                        @Override
                                        public void setDisposable(@Nullable Disposable d) {

                                        }

                                        @Override
                                        public void setCancellable(@Nullable Cancellable c) {

                                        }

                                        @Override
                                        public boolean isDisposed() {
                                            return false;
                                        }

                                        @Override
                                        public @io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Integer> serialize() {
                                            return null;
                                        }

                                        @Override
                                        public boolean tryOnError(@io.reactivex.rxjava3.annotations.NonNull Throwable t) {
                                            return false;
                                        }

                                        @Override
                                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull Integer value) {
                                            if (!mBottomUpgradeHintBinding.upgradeRomeProgress.isShown()) {
                                                mBottomUpgradeHintBinding.upgradeRomeProgress.show();
                                            }
                                            MyLog.printLog("当前类:HomeFragmentHandler,当前方法：onNext,当前线程:" + Thread.currentThread().getName() + ",信息:上传进度" + value);
                                            mBottomUpgradeHintBinding.upgradeRomeProgress.setProgress(value);
                                        }

                                        @Override
                                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable error) {
                                            MyLog.printLog("当前类:PolarisSocketHelper,当前方法：run,信息:上传发生错误" + error.getMessage());
                                            OrderCommunication.getInstance().SP_LOAD_UPGRADE_FW_STATE(-1);
                                            mBottomUpgradeHintBinding.upgradeRomeProgress.hide();
                                            PolarisSettings.uploading = false;
                                        }

                                        @Override
                                        public void onComplete() {
                                            MyLog.printLog("当前类:PolarisSocketHelper,当前方法：run,信息:上传完成");
                                            OrderCommunication.getInstance().SP_LOAD_UPGRADE_FW_STATE(0);
                                            mBottomUpgradeHintBinding.upgradeRomeProgress.hide();
                                            PolarisSettings.uploading = false;
                                            PolarisSettings.RomUpgradeIsSuccess = true;
                                        }
                                    }, updateFile, NetWorkUtil.INSTANCE.getPolarisLanNetWork());
                                    break;
                                }
                                case 1: {
                                    //去下载新版本
                                    PolarisNetWork.INSTANCE.downloadRom(PolarisSettings.SoftWarePolarisRomUrl, App.GlobalManager.INSTANCE.getContext(null), PolarisSettings.RomFileName, new PolarisNetWork.DownLoadListener() {
                                        @Override
                                        public void onStart() {

                                        }

                                        @Override
                                        public void onDownLoad(int loadProcess) {
                                            if (!mBottomUpgradeHintBinding.downloadProgress.isShown()) {
                                                mBottomUpgradeHintBinding.downloadProgress.show();
                                            }
                                            mBottomUpgradeHintBinding.downloadProgress.setProgress(loadProcess);
                                        }

                                        @Override
                                        public void onComplete(String md5, File file) {
                                            if (PolarisSettings.SoftWarePolarisRomMD5 != null && PolarisSettings.SoftWarePolarisRomMD5.equals(md5)) {
                                                HomeFragmentHandler.this.handleMessage(Message.obtain(HomeFragmentHandler.this,HomeFragmentHandler.WHAT_UPGRADE_START,0,0));
                                            } else {
                                                PolarisSettings.uploading = false;
                                            }
                                            mBottomUpgradeHintBinding.downloadProgress.hide();
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Toast.makeText(App.GlobalManager.INSTANCE.getContext(null), "下载失败", Toast.LENGTH_SHORT).show();
                                            mBottomUpgradeHintBinding.downloadProgress.hide();
                                            PolarisSettings.uploading = false;
                                        }
                                    });
                                    break;
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                });
                break;
            }
            default: {

            }
        }
    }
}
}
