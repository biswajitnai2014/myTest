package com.bis.mytest.bluetooth.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil

import androidx.recyclerview.widget.RecyclerView
import com.bis.mytest.R
import com.bis.mytest.bluetooth.utils.DeviceNameOnClickListner
import com.bis.mytest.databinding.LayoutdeviceListBinding


class DeviceListAdapter(
    private val context: Context,
    var bluetoothDeviceList: ArrayList<BluetoothDevice>,
    private val deviceNameOnClickListner: DeviceNameOnClickListner
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    inner class ViewHolder(val rowBinding: LayoutdeviceListBinding) :
        RecyclerView.ViewHolder(rowBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(bluetoothDevice: BluetoothDevice) {
            rowBinding.apply {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("TAG_a", "bind: ")
                }
                tvDevice.apply {
                    bluetoothDevice.name?.let {name->
                        text =name.toString()                    }

                    tvDevice.setOnClickListener {
                        deviceNameOnClickListner.showDevice(bluetoothDevice)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.layoutdevice_list,
                parent,
                false
            )
        )

    }

    override fun getItemCount(): Int {
        return bluetoothDeviceList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(bluetoothDeviceList[position])
    }
}