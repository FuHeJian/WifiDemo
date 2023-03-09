package com.example.wifidemo1.broadcaster

import android.content.BroadcastReceiver
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.wifidemo1.bluetooth.BlueToothUtil.WhenScanOnStop
import com.example.wifidemo1.helper.BlueToothScanHelper

/**
 * 协助注册广播的单例类
 * fhj
 */
object BroadcasterUtil {

    /**
     * 注册wifi广播，并自动注销广播
     */
    fun registerWiFiBroadCastReceiverForActivity(
        context: AppCompatActivity,
        receiver: BroadcastReceiver,
        flags: IntentFilter
    ) {
        registerSimpleReceiver(context, receiver, flags)
    }

    /**
     * 注册蓝牙广播，广播方式可以接收经典蓝牙和BLE广播，自动注销广播
     */
    fun registerBlueToothBroadCastReceiverForActivity(
        context: AppCompatActivity,
        receiver: BroadcastReceiver,
        flags: IntentFilter,
        whenScanOnStop: WhenScanOnStop
    ) {
        registerSimpleReceiver(context, receiver, flags)
        //开启扫描
        BlueToothScanHelper.scanBLE(context,whenScanOnStop)
    }

    /**
     * 协助注册广播的方法
     */
    private fun registerSimpleReceiver(
        context: AppCompatActivity,
        receiver: BroadcastReceiver,
        flags: IntentFilter
    ) {
        context.registerReceiver(receiver, flags)
        val observer = object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                context.unregisterReceiver(receiver)
                context.lifecycle.removeObserver(this)
            }
        }
        context.lifecycle.addObserver(observer)
    }

}