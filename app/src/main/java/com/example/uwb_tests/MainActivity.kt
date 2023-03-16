package com.example.uwb_tests

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {package com.example.uwb_test

    import android.bluetooth.BluetoothManager
    import android.content.Context
    import android.content.pm.PackageManager
    import android.os.Bundle
    import androidx.appcompat.app.AppCompatActivity

    class MainActivity : AppCompatActivity() {
        val packageManager: PackageManager = applicationContext.packageManager
        private val REQUEST_BLUETOOTH_PERMISSION = 1
        private val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE)
                    as BluetoothManager
        val bluetoothLeScanner =bluetoothManager.adapter.bluetoothLeScanner

        bluetoothLeScanner.startScan(...,)

        val deviceSupportUwb = packageManager.hasSystemFeature("andriod.hardware.uwb")


        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            if (deviceSupportUwb) {

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}