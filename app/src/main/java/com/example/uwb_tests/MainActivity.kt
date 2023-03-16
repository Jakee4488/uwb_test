package com.example.uwb_tests

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.uwb.RangingParameters
import androidx.core.uwb.RangingResult
import androidx.core.uwb.UwbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var uwbManager: UwbManager
    private val controllerSession by lazy { uwbManager.controllerSessionScope() }
    private val controleeSession by lazy { uwbManager.controleeSessionScope() }
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothLeScanner: BluetoothLeScanner

    private val REQUEST_BLUETOOTH_PERMISSION = 1
    private val serviceUuid = "<serviceUuid>" //replace with actual service UUID
    private val deviceAddress = "<deviceAddress>" //replace with actual device address
    private val sessionKey = "<sessionKey>" //replace with actual session key
    private val complexChannel = "<complexChannel>" //replace with actual channel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if the device has UWB hardware support
        val deviceSupportUwb = packageManager.hasSystemFeature("android.hardware.uwb")

        if (deviceSupportUwb) {
            // Initialize UWB manager
            uwbManager = UwbManager.createInstance(this)

            // Check for Bluetooth permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH),
                    REQUEST_BLUETOOTH_PERMISSION
                )
            } else {
                // Initialize Bluetooth scanner
                bluetoothManager =
                    getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
                bluetoothLeScanner = bluetoothManager.adapter.bluetoothLeScanner

                // Start scanning for devices
                bluetoothLeScanner.startScan(
                    null,
                    ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .build(),
                    object : ScanCallback() {
                        override fun onScanResult(callbackType: Int, result: ScanResult) {
                            super.onScanResult(callbackType, result)

                            // Get the service data of the UWB device
                            val serviceData = result.scanRecord?.getServiceData(serviceUuid)

                            // Connect to the UWB device
                            if (result.device.address == deviceAddress && serviceData != null) {
                                connectToDevice(serviceData)
                                bluetoothLeScanner.stopScan(this)
                            }
                        }
                    })
            }
        }
    }

    private fun connectToDevice(serviceData: ByteArray) {
        // Create ranging parameters for the UWB session
        val rangingParams = RangingParameters(
            uwbConfigType = RangingParameters.UWB_CONFIG_ID_1,
            sessionKeyInfo = sessionKey.toByteArray(),
            complexChannel = complexChannel.toByte(),
            peerDevices = listOf(deviceAddress),
            updateRate = RangingParameters.RANGING_UPDATE_RATE_FREQUENT
        )

        // Prepare UWB session
        val sessionFlow = if (uwbManager.isControleeSupported) {
            controleeSession.prepareSession(rangingParams)
        } else {
            controllerSession.prepareSession(rangingParams)
        }

        //


}

