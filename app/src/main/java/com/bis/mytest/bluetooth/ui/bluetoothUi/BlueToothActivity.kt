package com.bis.mytest.bluetooth.ui.bluetoothUi

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.bis.mytest.bluetooth.adapter.BluetoothDeviceAdapter
import com.bis.mytest.bluetooth.adapter.DeviceListAdapter
import com.bis.mytest.bluetooth.commonMethod.commonMethod.Companion.UUID_VALUE
import com.bis.mytest.bluetooth.commonMethod.commonMethod.Companion.bluetoothManager
import com.bis.mytest.bluetooth.commonMethod.commonMethod.Companion.permissionList
import com.bis.mytest.bluetooth.utils.BluetoothService
import com.bis.mytest.bluetooth.utils.DeviceNameOnClickListner
import com.bis.mytest.databinding.ActivityBlueToothBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class BlueToothActivity : AppCompatActivity() {
    val permissionsToRequest = mutableListOf<String>()
    lateinit var binding:ActivityBlueToothBinding
    private val  MY_PERMISSIONS_REQUEST_CODE=1
    var bluetoothDeviceList: ArrayList<BluetoothDevice> = ArrayList()
    private var serverSocket: BluetoothServerSocket?=null
    var deviceListAdapter: DeviceListAdapter? = null
    var bluetoothDeviceAdapter: BluetoothDeviceAdapter? = null
    var bluetoothAdapter: BluetoothAdapter? = null
    var bluetoothService: BluetoothService? = null

        private lateinit var socket: BluetoothSocket
    private lateinit var outputStream: OutputStream
    private lateinit var inputStream: InputStream
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityBlueToothBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    fun init() {
        binding.apply {
            bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            bluetoothAdapter = bluetoothManager?.adapter
            if (bluetoothAdapter == null) {
                Toast.makeText(
                    this@BlueToothActivity,
                    "Bluetooth not supported",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                /*permissionCheck()
                btnOfOff.setOnClickListener {*/
                    permissionCheck()
                    enableBlueTooth()
                //}

                btnDeviceList.setOnClickListener {


                    if (bluetoothAdapter?.isDiscovering == true) {
                        bluetoothAdapter?.cancelDiscovery()
                    }
                    val bool = bluetoothAdapter?.startDiscovery()
                    if (bool == true) {
                        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
                        registerReceiver(mReceiver, filter)
                    }
                }

                btnServer.setOnClickListener {
                    registerDevice()
                    val serverClass = bluetoothService?.ServerClass()
                    serverClass?.start()
                }

            }
            bluetoothDeviceAdapter =
                BluetoothDeviceAdapter(bluetoothDeviceList, object : DeviceNameOnClickListner {
                    override fun showDevice(device: BluetoothDevice) {
                        registerDevice()
                        val clientClass = bluetoothService?.ClientClass(device)
                        clientClass?.start()



                    }

                })

        recDeciceList.adapter = bluetoothDeviceAdapter

            btnShare.setOnClickListener {
                bluetoothService?.sendRecever?.write(
                    "Hello".toByteArray()
                )
            }
    }

    }






    @SuppressLint("MissingPermission")
    private fun enableBlueTooth() {
        if (bluetoothAdapter?.isEnabled == false) {
            //bluetoothAdapter?.enable()
            val intent = (Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            startActivity(intent)
            registerDevice()
        }
    }

    private fun registerDevice() {
         bluetoothAdapter?.let { bluetoothService=BluetoothService(it,this@BlueToothActivity) }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun permissionCheck() {
        for (permission in permissionList) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }
            /*if (permissionsToRequest.isNotEmpty()){
                ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toTypedArray(),
                    MY_PERMISSIONS_REQUEST_CODE
                )
            }
            else{
                enableBlueTooth()
                }*/
            Dexter.withContext(this)
                .withPermissions(permissionsToRequest)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        // Check if user has granted all
                        if (report?.areAllPermissionsGranted() == true) {
                            enableBlueTooth()
                        } else {
                            Toast.makeText(this@BlueToothActivity, "Permission deny", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        // User has denied a permission, proceed and ask them again
                        token?.continuePermissionRequest()
                    }
                }).check()

    }

    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    enableBlueTooth()
                } else {
                    Toast.makeText(this, "Permission deni", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }*/

    private val mReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                // A Bluetooth device was found
                // Getting device information from the intent
                val device =
                    intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                Log.e("TAG_error", "onReceive: " + device?.name)
                //deviceListAdapter?.let { adapter ->
                    device?.let { device ->
                        if (!bluetoothDeviceList.contains(device)) {
                            bluetoothDeviceList.add(device)
                        }
                        bluetoothDeviceAdapter?.notifyDataSetChanged()
                       // adapter.bluetoothDeviceList.add(device)
                       // adapter.bluetoothDeviceList = adapter.bluetoothDeviceList
                    }



              //  }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(mReceiver)
           // unregisterReceiver(receiver)
            socket.close()
        }catch (io:IOException){}
        catch (e:Exception){}
    }


}