package com.bis.mytest.bluetooth.utils



import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class BService(
    val adapter: BluetoothAdapter,
    val ctx: Context
) {
    private var sendReceiver: SendReceiver? = null

    inner class SendReceiver(private val bluetoothSocket: BluetoothSocket?) {
        private var inputStream: InputStream? = null
        private var outputStream: OutputStream? = null

        suspend fun start() {
            withContext(Dispatchers.IO) {
                var tempIn: InputStream? = null
                var tempOut: OutputStream? = null
                try {
                    bluetoothSocket?.let {
                        tempIn = it.inputStream
                        tempOut = it.outputStream
                    }
                } catch (io: IOException) {
                } catch (e: Exception) {
                }

                tempIn?.let {
                    inputStream = it
                }
                tempOut?.let {
                    outputStream = it
                }

                val buffer = ByteArray(1024)
                var bytes: Int? = null
                while (true) {
                    try {
                        bytes = inputStream?.read(buffer)
                        if (bytes != null) {
                            // Handle received data here
                            // You can use Kotlin channels or callbacks to notify the caller
                        }
                    } catch (io: IOException) {
                        // Handle IO exception
                    } catch (e: Exception) {
                        // Handle other exceptions
                    }
                }
            }
        }

        suspend fun write(byte: ByteArray) {
            withContext(Dispatchers.IO) {
                try {
                    outputStream?.write(byte)
                } catch (io: IOException) {
                    // Handle IO exception
                } catch (e: Exception) {
                    // Handle other exceptions
                }
            }
        }
    }
}