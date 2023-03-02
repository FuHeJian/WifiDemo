package com.example.wifidemo1.brocaster

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.wifidemo1.helper.BlueToothScanHelper

object Util {

    fun registerWiFiBroadCastReceiverForActivity(
        context: AppCompatActivity,
        receiver: BroadcastReceiver,
        flags: IntentFilter
    ) {
        registerSimpleReceiver(context, receiver, flags)
    }

    fun registerBlueToothBroadCastReceiverForActivity(
        context: AppCompatActivity,
        receiver: BroadcastReceiver,
        flags: IntentFilter
    ) {
        registerSimpleReceiver(context, receiver, flags)
        BlueToothScanHelper.scanBLE(context)
    }

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