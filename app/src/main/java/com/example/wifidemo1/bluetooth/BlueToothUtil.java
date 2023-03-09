package com.example.wifidemo1.bluetooth;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.wifidemo1.activity.base.BaseActivity;
import com.example.wifidemo1.broadcaster.BlueToothReceiver;
import com.example.wifidemo1.broadcaster.BroadcasterUtil;
import com.example.wifidemo1.log.MyLog;
import com.example.wifidemo1.permission.PermissionUtil;
import com.example.wifidemo1.activity.RequestCode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import kotlin.jvm.internal.Intrinsics;

import java.security.PrivilegedAction;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author: fuhejian
 * @date: 2023/3/3
 */
public class BlueToothUtil {

    public static final BlueToothUtil INSTANCE;

    /**
     * "搜索不到手机设备，待解决。使用广播的方式可以发现手机，目前本方法只用于启动蓝牙的广播的接收"
     * 参数作用查看 {@link #makeOpenBLE}
     * @see #makeOpenBLE
     */
    public final void scanBLE(@NotNull Context context, @NotNull List<android.bluetooth.le.ScanFilter> filter, @NotNull ScanSettings settings, @Nullable ScanCallback callback, long scanTime, WhenScanOnStop onStop) {
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(filter, "filter");
        Intrinsics.checkNotNullParameter(settings, "settings");
        this.makeOpenBLE(context, filter, settings, callback, scanTime, true, onStop);
    }

    /**
     * 确保蓝牙打开。可以设置scanBLE为true,用于当蓝牙开启时自动调用{@link #scanBLEWithBLEEnabled}。
     *
     * @param context
     * @param filter callback为null,本参数无作用可设置为null
     * @param settings  callback为null,本参数无作用可设置为null
     * @param callback 扫描BLE设备的回调，用于传递给{@link #scanBLEWithBLEEnabled}
     * @param scanTime 扫描时间，用于传递给{@link #scanBLEWithBLEEnabled}
     * @param scanBLE  打开蓝牙时，是否启动扫描
     *
     * @see BluetoothLeScanner#startScan(List filter, ScanSettings settings, ScanCallback callback)
     */

    @SuppressLint("MissingPermission")
    private final void makeOpenBLE(final Context context, List<android.bluetooth.le.ScanFilter> filter, ScanSettings settings, final ScanCallback callback, final long scanTime, final boolean scanBLE, WhenScanOnStop onStop) {
        Object var10000 = context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (!(var10000 instanceof BluetoothManager)) {
            var10000 = null;
        }

        BluetoothManager BLUETOOTH = (BluetoothManager) var10000;
        BluetoothAdapter adapter = BLUETOOTH != null ? BLUETOOTH.getAdapter() : null;
        if (BLUETOOTH != null && adapter != null) {
            //检查权限
            if (!PermissionUtil.checkBlueToothCONNECT(context)) {
                return;
            }

            if (!adapter.isEnabled()) {
                MyLog.printLog("打开蓝牙");
/*                if (PermissionUtil.isHighAndroidTIRAMISU()) {
                } else if (adapter.enable() && scanBLE) {
                    this.scanBLEWithBLEEnabled(context, callback, scanTime, onStop);
                }*/
                Intent intent = new Intent();
                intent.setAction("android.bluetooth.adapter.action.REQUEST_ENABLE");

                if (context instanceof BaseActivity) {
                    ((BaseActivity) context).addRegisterForActivityResultListener(new BaseActivity.RegisterForActivityResultListener() {
                        @Override
                        public void onResult(ActivityResult result) {
                            if (result != null) {
                                if (result.getResultCode() == -1) {
                                    if (scanBLE) {
                                        BlueToothUtil.INSTANCE.scanBLEWithBLEEnabled(context, filter,settings,callback, scanTime, onStop);
                                    }
                                } else {
                                    Toast.makeText(context, (CharSequence) "未打开蓝牙", Toast.LENGTH_SHORT).show();
                                }
                            }
                            ((BaseActivity) context).removeRegisterForActivityResultListener(this);
                        }
                    });
                    ActivityResultLauncher<Intent> launcher = ((BaseActivity) context).getRegisterForActivityResult();
                    if (launcher != null) {
                        launcher.launch(intent);
                    }
                } else {
                    context.startActivity(intent);
                }

            } else if (scanBLE) {
                //蓝牙已打开
                BlueToothUtil.INSTANCE.scanBLEWithBLEEnabled(context, filter,settings,callback, scanTime, onStop);
            }
        }
    }

    /**
     * 当前设备是否支持BLE
     * @param packageManager
     * @return
     */
    private final boolean isSupportBLE(PackageManager packageManager) {
        return packageManager.hasSystemFeature("android.hardware.bluetooth_le");
    }

    /**
     * 返回 mac + add
     *
     * @param mac
     * @param add
     * @return
     */
    @Nullable
    public final String getMacAdd(@NotNull String mac, int add) {

        Intrinsics.checkNotNullParameter(mac, "mac");
        try {
            int var6 = mac.length() - 1;
            String var10000 = mac.substring(var6);
            Intrinsics.checkNotNullExpressionValue(var10000, "this as java.lang.String).substring(startIndex)");
            String var5 = var10000;
            Locale var15 = Locale.getDefault();
            Intrinsics.checkNotNullExpressionValue(var15, "Locale.getDefault()");
            Locale var10 = var15;
            var10000 = var5.toUpperCase(var10);
            Intrinsics.checkNotNullExpressionValue(var10000, "this as java.lang.String).toUpperCase(locale)");
            String lastChar = var10000;
            byte var11 = 0;
            int var7 = mac.length() - 1;
            var10000 = mac.substring(var11, var7);
            Intrinsics.checkNotNullExpressionValue(var10000, "this as java.lang.String…ing(startIndex, endIndex)");
            mac = var10000;
            if (Intrinsics.areEqual("F", lastChar)) {
                var10000 = "0";
            } else {
                byte var13 = 16;
                int tempChar = Integer.parseInt(lastChar, var13) + add;
                var10000 = Integer.toHexString(tempChar);
                Intrinsics.checkNotNullExpressionValue(var10000, "Integer.toHexString(tempChar)");
                String var12 = var10000;
                var15 = Locale.getDefault();
                Intrinsics.checkNotNullExpressionValue(var15, "Locale.getDefault()");
                Locale var14 = var15;
                var10000 = var12.toUpperCase(var14);
                Intrinsics.checkNotNullExpressionValue(var10000, "this as java.lang.String).toUpperCase(locale)");
            }

            lastChar = var10000;
            return mac + lastChar;
        } catch (Exception var8) {
            return null;
        }
    }

    /**
     * 开启蓝牙搜索附近设备，使可以收到外部广播
     * @param context
     * @param callback 设置为null,使用广播接收，不为null,广播和ScanCallback回调都会接收到外部设备的广播，ScanCallback只会收到BLE设备的广播。
     * @param scanTime
     * @param onStop
     */
    @SuppressLint("MissingPermission")
    public final void scanBLEWithBLEEnabled(@NotNull Context context,List<android.bluetooth.le.ScanFilter> filter, ScanSettings settings, @Nullable ScanCallback callback, long scanTime, WhenScanOnStop onStop) {
        Intrinsics.checkNotNullParameter(context, "context");
        Object var10000 = context.getSystemService(Context.BLUETOOTH_SERVICE);
        if (!(var10000 instanceof BluetoothManager)) {
            var10000 = null;
        }

        BluetoothManager BLUETOOTH = (BluetoothManager) var10000;
        BluetoothAdapter adapter = BLUETOOTH != null ? BLUETOOTH.getAdapter() : null;
        MyLog.printLog("当前类:BlueToothUtil,信息: 扫描：" + adapter.isDiscovering());
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
        if (adapter != null) {
            BluetoothLeScanner bluetoothLeScanner = adapter.getBluetoothLeScanner();
            if (callback != null && bluetoothLeScanner != null) {
                bluetoothLeScanner.startScan(filter,settings,callback);
            }
            if (ActivityCompat.checkSelfPermission(context, "android.permission.BLUETOOTH_SCAN") != 0 && android.os.Build.VERSION.SDK_INT >= 31) {
                Toast.makeText(context, "未获取BLUETOOTH_SCAN权限", Toast.LENGTH_SHORT).show();
                MyLog.printLog("未获取BLUETOOTH_SCAN权限");
            } else {
                MyLog.printLog("开始扫描");
                boolean startResult = adapter.startDiscovery();
                MyLog.printLog("当前类:BlueToothUtil,信息: 启动蓝牙搜索：" + startResult);
                if (scanTime >= 0L) {
                    AndroidSchedulers.mainThread().scheduleDirect(() -> {
                        if (callback != null) {
                            bluetoothLeScanner.stopScan(callback);
                        }
                        if (onStop != null) {
                            onStop.whenScanOnStop(adapter);
                        }
                        adapter.cancelDiscovery();
                    }, scanTime, TimeUnit.MILLISECONDS);
                }
            }
        }

    }

    private BlueToothUtil() {
    }

    static {
        INSTANCE = new BlueToothUtil();
    }

    /**
     * 设置到达scan扫描时间的回调
     */
    public interface WhenScanOnStop {
        void whenScanOnStop(BluetoothAdapter adapter);
    }


    /**
     *
     * @param listener 看这个{@link BlueToothReceiver.ReceiverListener}
     * @param context
     * @param whenScanOnStop 看这个{@link WhenScanOnStop}
     */
    public static void registerBlueToothReceiver(BlueToothReceiver.ReceiverListener listener, AppCompatActivity context, WhenScanOnStop whenScanOnStop) {
        BlueToothReceiver blueToothReceiver = new BlueToothReceiver(listener);
        IntentFilter flags = new IntentFilter();
        flags.addAction(BluetoothDevice.ACTION_FOUND);
        BroadcasterUtil.INSTANCE.registerBlueToothBroadCastReceiverForActivity(context, blueToothReceiver, flags, whenScanOnStop);
    }

}
