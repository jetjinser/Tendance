package com.github.jetjinser.tendance

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.jetjinser.tendance.data.AppContainer
import com.github.jetjinser.tendance.service.ble.BLEService
import com.github.jetjinser.tendance.ui.TendanceApp


class MainActivity : ComponentActivity() {
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
    }

    private var mAppContainer: AppContainer? = null

    var bleService: BLEService? = null
    private val serviceConnection = object : ServiceConnection {
        @RequiresApi(Build.VERSION_CODES.S)
        @RequiresPermission(
            allOf = [
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            ]
        )
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "Service Connected")

            mAppContainer?.also { appContainer ->
                bleService = (service as BLEService.LocalBinder).getService(appContainer)
            } ?: run {
                Log.d(TAG, "app container not initialized")
                return
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "Service Disconnected")
            bleService = null
        }
    }

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(
        allOf = [Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT]
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        // enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val appContainer = (application as TendanceApplication).container
        mAppContainer = appContainer

        setContent {
            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
            TendanceApp(appContainer = appContainer, widthSizeClass = widthSizeClass)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d(TAG, "requested permission")
            } else {
                Log.d(TAG, "Should request permission")
            }
        }

        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "Permission OK")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.BLUETOOTH_CONNECT
            ) -> {
                Log.d(TAG, "educate user")
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            }
        }

        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d(TAG, "Permission OK")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.BLUETOOTH_SCAN
            ) -> {
                Log.d(TAG, "educate user")
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.BLUETOOTH_SCAN
                )
            }
        }

        val gattServiceIntent = Intent(this, BLEService::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }
}
