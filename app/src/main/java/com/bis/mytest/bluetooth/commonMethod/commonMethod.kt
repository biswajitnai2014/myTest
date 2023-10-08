package com.bis.mytest.bluetooth.commonMethod

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.os.Build
import androidx.annotation.RequiresApi

class commonMethod {
    companion object{
        var UUID_VALUE="00001101-0000-1000-8000-00805F9B34FB"
        var bluetoothAdapter: BluetoothAdapter?=null
        var bluetoothManager: BluetoothManager?=null
        @RequiresApi(Build.VERSION_CODES.S)
        val permissionList = arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_ADMIN)
    }
}