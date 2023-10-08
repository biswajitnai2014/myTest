package com.bis.mytest.bluetooth.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bis.mytest.bluetooth.utils.DeviceNameOnClickListner
import com.bis.mytest.databinding.DeviceListItemLayoutBinding

class BluetoothDeviceAdapter(
    var bluetoothDeviceList: ArrayList<BluetoothDevice>,
    private val deviceNameOnClickListner: DeviceNameOnClickListner
):RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BluetoothDeviceAdapter.ViewHolder {
       val binding =DeviceListItemLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BluetoothDeviceAdapter.ViewHolder, position: Int) {
        val item=bluetoothDeviceList.get(position)
        holder.bind(item)
        holder.itemView.setOnClickListener{
            Toast.makeText(holder.itemView.context, "", Toast.LENGTH_SHORT).show()
            deviceNameOnClickListner.showDevice(item)
        }
    }

    override fun getItemCount(): Int=bluetoothDeviceList.size
    class ViewHolder(private val binding: DeviceListItemLayoutBinding):RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("MissingPermission")
        fun bind(item: BluetoothDevice) {
            item.name?.let {deviceName->
                binding.tvDevice.text = deviceName
            }

        }

    }
}