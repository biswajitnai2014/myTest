package com.bis.mytest.bluetooth.utils

import android.bluetooth.BluetoothDevice

interface DeviceNameOnClickListner {
    fun showDevice(device: BluetoothDevice)
}