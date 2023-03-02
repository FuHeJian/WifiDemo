package com.example.wifidemo1

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.companion.BluetoothDeviceFilter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.PermissionResult
import androidx.core.content.pm.PackageInfoCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wifidemo1.adapter.DevicesListAdapter
import com.example.wifidemo1.brocaster.BlueToothReceiver
import com.example.wifidemo1.brocaster.Util
import com.example.wifidemo1.brocaster.WiFiReceiverJava
import com.example.wifidemo1.databinding.ActivityMainBinding
import com.example.wifidemo1.helper.BlueToothScanHelper
import com.example.wifidemo1.log.MyLog
import com.example.wifidemo1.permission.PermissionUtil
import kotlin.contracts.contract

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.DevicesList.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        val adapter =  DevicesListAdapter(object :
            DiffUtil.ItemCallback<BluetoothDevice>() {
            @SuppressLint("MissingPermission")
            override fun areItemsTheSame(
                oldItem: BluetoothDevice,
                newItem: BluetoothDevice
            ): Boolean {
                if (!PermissionUtil.checkBlueToothCONNECT(this@MainActivity)) {
                    return true
                }
                if(oldItem.name!=null&&newItem.name!=null){
                    return oldItem.name.equals(newItem.name)
                }
                return false
            }
            @SuppressLint("MissingPermission")
            override fun areContentsTheSame(
                oldItem: BluetoothDevice,
                newItem: BluetoothDevice
            ): Boolean {
                if (!PermissionUtil.checkBlueToothCONNECT(this@MainActivity)) {
                    return true
                }
                if(oldItem.name!=null&&newItem.name!=null){
                    return oldItem.name.equals(newItem.name)
                }
                return false
            }

        })
        binding.DevicesList.adapter = adapter

        registerBlueToothReceiver {
            if("polaris_2d3b07".equals(it.name)){
                val list = arrayListOf<BluetoothDevice>(it)
                adapter.submitList(list)
            }
        }

        //wifi配置
//        registerWiFiReceiver()

        //获取权限

        getPermission()

    }

    private fun registerBlueToothReceiver(listener: BlueToothReceiver.ReceiverListener){
        val blueToothReceiver = BlueToothReceiver(listener)
        val flags = IntentFilter()
        flags.addAction(BluetoothDevice.ACTION_FOUND)
        Util.registerBlueToothBroadCastReceiverForActivity(this,blueToothReceiver,flags)
    }

    /**
     * 注册WIFI广播
     */
    private fun registerWiFiReceiver() {
        val receiver = WiFiReceiverJava()
        val flags = IntentFilter()
        flags.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        flags.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        //activity销毁自动取消注册
        Util.registerWiFiBroadCastReceiverForActivity(this, receiver, flags)
    }

    /**
     * 获取权限
     */
    private fun getPermission() {
        val permissions = arrayOf(
            "android.permission.CHANGE_WIFI_STATE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.CHANGE_NETWORK_STATE",
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_SCAN",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.BLUETOOTH_ADVERTISE",
            "android.permission.BLUETOOTH_CONNECT",
            Manifest.permission.ACCESS_NETWORK_STATE
        )
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q)
        {
            permissions.plus("android.permission.ACCESS_FINE_LOCATION")
        }
        val requestCode = 66
        ActivityCompat.requestPermissions(this,permissions,requestCode)
    }

    /**
     * 权限授权回调
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        permissions.forEachIndexed{
            index,str->
                MyLog.printLog(str+"授权结果"+(grantResults[index]== PERMISSION_GRANTED))
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


}