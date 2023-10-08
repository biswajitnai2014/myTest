package com.bis.mytest.bluetooth.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.bis.mytest.bluetooth.commonMethod.commonMethod.Companion.UUID_VALUE
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID


class BluetoothService(
    val adapter: BluetoothAdapter,

    val ctx: Context
):Thread() {
     var sendRecever: SendRecever?=null

    inner class ServerClass:Thread(){
       private var serverSocket:BluetoothServerSocket?=null
        override fun run() {
            super.run()
            var socket:BluetoothSocket?=null
            while (socket==null){
                try {

                    socket=serverSocket?.accept()
                }catch (io:IOException){

                }
                catch (e:Exception){

                }
                if(socket!=null){

                    sendRecever= SendRecever(socket)
                    sendRecever?.start()
                    break
                }
            }

        }
        init{
            try{
                if (ActivityCompat.checkSelfPermission(
                        ctx,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                serverSocket=adapter.listenUsingRfcommWithServiceRecord("Chat App", UUID.fromString(UUID_VALUE))
            }catch (io:IOException){

            }catch (e:Exception){

            }
        }




    }


    inner class ClientClass(private val bluetoothDevice: BluetoothDevice):Thread(){
        private var socket:BluetoothSocket?=null
        override fun run() {
            super.run()
            try {
                if (ActivityCompat.checkSelfPermission(
                        ctx,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                socket?.let {

                    it.connect()


                    sendRecever = SendRecever(it)
                    sendRecever?.start()
                }
            }catch (io:IOException){

                Log.d("TAG_e", "run:IO "+io.message)
            }
            catch (e:Exception){
                Log.d("TAG_e", "run:e "+e.message)

            }
        }
        init {
            try {
                if (ActivityCompat.checkSelfPermission(
                        ctx,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {}
                socket=bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUID_VALUE))
            }catch (io:IOException){}
            catch (e:Exception){}
        }


    }
    inner class SendRecever(private val bluetoothSocket: BluetoothSocket?):Thread(){
        private  var inputStream:InputStream?=null
        private  var outputStream:OutputStream?=null

        @RequiresApi(Build.VERSION_CODES.Q)
        override fun run() {
            super.run()
            val buffer=ByteArray(1024)
            var bytes:Int?=null
            while (true) {
                try {
                    bytes = inputStream?.read(buffer)
                    if (bytes != null) {
                        Log.d("TAG_msg", "run: ok")
                        // handler.obtainMessage(STATE_NESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget()
                    }
                } catch (io: IOException) {

                } catch (e: Exception) {

                }

            }
            }
        fun write(byte: ByteArray){
            try {
                outputStream?.write(byte)
            }catch (io:IOException){
                Log.d("TAG_hh", "write: A")
            }
            catch (e:Exception){
                Log.d("TAG_hh", "write: B")
            }
        }

        init {
            var tempIn:InputStream?=null
            var tempOut:OutputStream?=null
            try {
                bluetoothSocket?.let {
                    tempIn=it.inputStream
                    tempOut=it.outputStream
                }

            }   catch (io:IOException){}
            catch (e:Exception){}

            tempIn?.let{

            }
            tempIn?.let {
                inputStream =it
            }
            tempOut?.let {
                outputStream=it
            }
        }
}
}