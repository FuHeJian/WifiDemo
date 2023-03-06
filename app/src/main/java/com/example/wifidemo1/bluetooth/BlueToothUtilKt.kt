package com.example.wifidemo1.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.wifidemo1.log.MyLog
import com.example.wifidemo1.permission.PermissionUtil
import com.example.wifidemo1.activity.RequestCode
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import java.util.*
import java.util.concurrent.TimeUnit


@Deprecated("改用java版的BlueToothUtil")
object BlueToothUtilKt {

    /**
     * startScan()方式扫描蓝牙设备
     */
    @Deprecated("搜索不到手机设备，待解决。使用广播的方式可以发现手机，目前本方法只用于启动蓝牙的广播的接收")
    fun scanBLE(
        context: Context,
        filter: List<ScanFilter>,
        settings: ScanSettings,
        callback: ScanCallback?,
        scanTime: Long,
    ) {
        makeOpenBLE(context, filter, settings, callback, scanTime, true)
    }

    /**
     * 尝试打开蓝牙
     */
    @SuppressLint("MissingPermission")
    private fun makeOpenBLE(
        context: Context,
        filter: List<ScanFilter>,
        settings: ScanSettings,
        callback: ScanCallback?,
        scanTime: Long,
        scanBLE: Boolean
    ) {
        val BLUETOOTH = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = BLUETOOTH?.adapter
        if (BLUETOOTH != null && adapter != null) {
            if (!PermissionUtil.checkBlueToothCONNECT(context)) {
                return
            }

            if (!adapter.isEnabled) {
                MyLog.printLog("打开蓝牙")
                if (PermissionUtil.isHighAndroidTIRAMISU()) {
                    val intent = Intent()
                    intent.action = BluetoothAdapter.ACTION_REQUEST_ENABLE
                    if (context is AppCompatActivity) {
                        val openBLEActivityLauncher =
                            (context as AppCompatActivity).registerForActivityResult(
                                ActivityResultContracts.StartIntentSenderForResult(),
                                object : ActivityResultCallback<ActivityResult> {
                                    override fun onActivityResult(result: ActivityResult?) {
                                        result?.let {
                                            if (result.resultCode == RESULT_OK) {
                                                if (scanBLE) {
                                                    scanBLEWithBLEEnabled(
                                                        context,
                                                        callback,
                                                        scanTime
                                                    )
                                                }

                                            } else {
                                                Toast.makeText(context, "未打开蓝牙", Toast.LENGTH_SHORT)
                                                    .show()
                                            }
                                        }
                                    }
                                })
                        openBLEActivityLauncher.launch(
                            IntentSenderRequest.Builder(
                                PendingIntent.getActivities(
                                    context, RequestCode.BLUE_ENABLE,
                                    arrayOf(intent), PendingIntent.FLAG_UPDATE_CURRENT
                                )
                            ).build()
                        )
                    } else {
                        //默认请求 code = -1
                        context.startActivity(intent)
                    }
                } else {
                    if (adapter.enable() && scanBLE) {
                        scanBLEWithBLEEnabled(context, callback, scanTime)
                    }
                }
            } else {
                if (scanBLE) {
                    scanBLEWithBLEEnabled(
                        context,
                        callback,
                        scanTime
                    )
                }
            }
        }
    }

    /**
     * 设备是否支持BLE
     */
    private fun isSupportBLE(packageManager: PackageManager) =
        packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)


    /**
     * 蓝牙地址转wifi->BSSID
     */
    fun getMacAdd(mac: String, add: Int): String? {
        var mac = mac
        try {
            var lastChar = mac.substring(mac.length - 1).uppercase(Locale.getDefault())
            mac = mac.substring(0, mac.length - 1)
            lastChar = if ("F" == lastChar) {
                "0"
            } else {
                val tempChar = lastChar.toInt(16) + add
                Integer.toHexString(tempChar).uppercase(Locale.getDefault())
            }
            return mac + lastChar
        } catch (e: Exception) {
        }
        return null
    }

    /**
     * 蓝牙打开后再调用
     *
     */
    public fun scanBLEWithBLEEnabled(
        context: Context,
        callback: ScanCallback?,
        scanTime: Long,
    ) {

        val BLUETOOTH = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        val adapter = BLUETOOTH?.adapter
        MyLog.printLog(adapter.toString())
        adapter?.let {
            val bluetoothLeScanner = adapter.bluetoothLeScanner
            //android 12 以上需要BLUETOOTH_SCAN权限
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S
            ) {
                MyLog.printLog("未获取BLUETOOTH_SCAN权限")
                return
            }
            //使广播可以接收到经典蓝牙和BLE蓝牙，startScan搜索不到手机，应该是手机用到了经典蓝牙吧
            MyLog.printLog("开始扫描")
            //开启蓝牙搜索广播接收
            adapter.startDiscovery()
//            bluetoothLeScanner.startScan(filter, settings, callback)//搜索BLE设备
            if (scanTime >= 0) {

                //在主线程中延迟执行
                AndroidSchedulers.mainThread().scheduleDirect({
                    if(callback!=null){
                        bluetoothLeScanner.stopScan(callback)
                    }
                    adapter.cancelDiscovery()
                },scanTime,TimeUnit.MILLISECONDS)

            }
            return
        }
        return
    }



}