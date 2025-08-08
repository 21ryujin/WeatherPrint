package com.weatherprint

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.util.Log
import java.io.IOException
import java.util.UUID
import com.weatherprint.ConstantParameters.Companion.SPP_UUID
import com.weatherprint.ConstantParameters.Companion.V2_PRO_PRINTER

/**
 * Bluetooth接続クラス
 */
class ConnectThread(device: BluetoothDevice, weatherData: WeatherData, deviceName: String) : Thread() {

    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(SPP_UUID))
    }

    // 印刷データ
    val printData: WeatherData = weatherData

    // 接続プリンター
    val strPrinter: String = deviceName

    override fun run() {
        // Cancel discovery because it otherwise slows down the connection.
        // bluetoothAdapter.cancelDiscovery()
        if (mmSocket == null) {
            return
        }
        val socket = mmSocket
        socket ?: return
        socket.connect()
        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        if (strPrinter == V2_PRO_PRINTER) {
            manageMyConnectedSocket(socket, printData)
        } else {
            manageMyConnectedSocketMPTII(socket, printData)
        }
        cancel()
    }

    // Closes the client socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Could not close the client socket", e)
        }
    }
}

// SUNMI V series 向け印刷実行
fun manageMyConnectedSocket(socket: BluetoothSocket, printData: WeatherData) {
    val connectedThread = ConnectedThread(socket, printData)
    connectedThread.start()
    connectedThread.join()
}

// MPT-II 向け印刷実行（英語のみ・簡易版印刷）
fun manageMyConnectedSocketMPTII(socket: BluetoothSocket, printData: WeatherData) {
    val ConnectedThreadMPTII = ConnectedThreadMPTII(socket, printData)
    ConnectedThreadMPTII.start()
    ConnectedThreadMPTII.join()
}