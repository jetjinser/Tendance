package com.github.jetjinser.tendance.service.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import com.github.jetjinser.tendance.data.AppContainer
import com.github.jetjinser.tendance.data.Device
import kotlinx.parcelize.Parcelize
import java.util.UUID


class BLEService : LifecycleService() {
    companion object {
        private val TAG: String = BLEService::class.java.simpleName

        const val EXTRA_DATA = "com.github.jetjinser.tendance.EXTRA_DATA"
    }

    private var appContainer: AppContainer? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(
        allOf = [
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        ]
    )
    override fun onCreate() {
        super.onCreate()
        initialize()
        registerReceiver(receiver, makeActionFilter(), RECEIVER_NOT_EXPORTED)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        close()
    }


    private val receiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        @RequiresPermission(
            allOf = [
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
            ]
        )
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                BLEAction.CONNECT_DEVICE.value -> {
                    intent.getStringExtra(EXTRA_DATA)?.also { address ->
                        this@BLEService.connect(address)
                    } ?: run {
                        Log.w(TAG, "cannot get extra address from intent")
                    }
                }

                BLEAction.START_SCAN_BLE.value -> {
                    this@BLEService.startScanLeDevices(scanCallback)
                }

                BLEAction.STOP_SCAN_BLE.value -> {
                    this@BLEService.stopScanLeDevices(scanCallback)
                }

                BLEAction.READ_CHARACTERISTIC.value -> {
                    intent.getParcelableExtra(EXTRA_DATA, UUID::class.java)
                        ?.also { uuid ->
                            this@BLEService.readCharacteristic(uuid)
                        } ?: run {
                        Log.w(TAG, "cannot get extra characteristic from intent")
                    }
                }

                BLEAction.WRITE_CHARACTERISTIC.value -> {
                    intent.getParcelableExtra(EXTRA_DATA, WritePackage::class.java)
                        ?.also { pkg ->
                            this@BLEService.writeCharacteristic(
                                pkg.uuid,
                                pkg.value
                            )
                        } ?: run {
                        Log.w(TAG, "cannot get extra WritePackage from intent")
                    }
                }

                BLEAction.SET_CHARACTERISTIC_NOTIFICATION.value -> {
                    intent.getParcelableExtra(EXTRA_DATA, SetNotificationPackage::class.java)
                        ?.also { pkg ->
                            this@BLEService.setCharacteristicNotification(
                                pkg.uuid,
                                pkg.enable
                            )
                        } ?: run {
                        Log.w(TAG, "cannot get extra characteristic from intent")
                    }
                }
            }
        }
    }

    private fun makeActionFilter() = IntentFilter().apply {
        BLEAction.entries.forEach {
            addAction(it.value)
        }
    }


    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        @RequiresApi(Build.VERSION_CODES.S)
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            Log.d(TAG, "onConnectionStateChange")
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    // successfully connected to the GATT Server
                    Log.d(TAG, "gatt connected")

                    bluetoothGatt?.discoverServices() ?: {
                        Log.d(TAG, "doesn't hold gatt, can't discover services")
                    }
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    // disconnected from the GATT Server
                    Log.d(TAG, "gatt disconnected")
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            Log.d(TAG, "onServiceDiscovered")
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    bluetoothGatt?.device?.also {
                        val device = Device(it.name, it.address)

                        getSupportedGattServices()?.forEach { service ->
                            if (service != null) {
                                appContainer?.consoleRepository?.addService(device, service)
                            }
                        }
                    }
                }

                else -> {
                    Log.w(TAG, "onServicesDiscovered received: $status")
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            Log.d(TAG, "onCharacteristicRead")
            when (status) {
                BluetoothGatt.GATT_SUCCESS -> {
                    Log.d(TAG, "read: ${value.decodeToString()}")
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray
        ) {
            Log.d(TAG, "onCharacteristicChanged")
        }
    }
    private val scanCallback = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.S)
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)

            result?.device?.let { bleDevice ->
                val device = Device(bleDevice.name ?: "Unknown", bleDevice.address)
                Log.d(TAG, "scanned device: $device")
                appContainer?.devicesRepository?.addDevice(device) ?: run {
                    Log.d(TAG, "no appContainer")
                }
            }
        }
    }

    private var bluetoothManager: BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null


    private var connectedAddress = ""


    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(
        allOf = [
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        ]
    )
    fun initialize(): Boolean {
        bluetoothManager = bluetoothManager ?: getSystemService<BluetoothManager>() ?: run {
            Log.e(TAG, "Unable to initialize BluetoothManager.")
            return false
        }

        bluetoothAdapter = bluetoothManager?.adapter ?: run {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }

        return true
    }


    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(address: String): Boolean {
        bluetoothGatt?.let {
            // Previously connected device.  Try to reconnect.
            if (connectedAddress == address) {
                Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.")
                return it.connect()
            }
        }

        val device = bluetoothAdapter?.getRemoteDevice(address) ?: run {
            Log.w(TAG, "Device not found.  Unable to connect.")
            return false
        }
        bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
        Log.d(TAG, "connected: ${bluetoothGatt?.device}")
        return true
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScanLeDevices(scanCallback: ScanCallback): Boolean {
        return bluetoothAdapter?.bluetoothLeScanner?.startScan(scanCallback)?.run {
            true
        } ?: run {
            Log.e(TAG, "no bluetooth available")
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun stopScanLeDevices(scanCallback: ScanCallback): Boolean {
        return bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)?.run {
            true
        } ?: run {
            Log.e(TAG, "no bluetooth available")
            false
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun readCharacteristic(uuid: UUID) {
        getSupportedGattServices()?.first {
            it?.getCharacteristic(uuid) != null
        }?.also {
            val char = it.getCharacteristic(uuid)
            Log.d(TAG, "received: ${char.service}")
            val suc = bluetoothGatt?.readCharacteristic(char) ?: run {
                Log.w(TAG, "BluetoothGatt not initialized")
                return
            }
            Log.d(TAG, "read success: $suc")
        } ?: run {
            Log.w(TAG, "no characteristic that uuid are: $uuid")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun writeCharacteristic(uuid: UUID, value: ByteArray) {
        getSupportedGattServices()?.first {
            it?.getCharacteristic(uuid) != null
        }?.also {
            val char = it.getCharacteristic(uuid)
            bluetoothGatt?.writeCharacteristic(
                char, value, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            ) ?: run {
                Log.w(TAG, "BluetoothGatt not initialized")
                return
            }
        } ?: run {
            Log.w(TAG, "no characteristic that uuid are: $uuid")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun setCharacteristicNotification(
        uuid: UUID,
        enabled: Boolean
    ) {
        getSupportedGattServices()?.first {
            it?.getCharacteristic(uuid) != null
        }?.also {
            val char = it.getCharacteristic(uuid)
            val isSuccess =
                bluetoothGatt?.setCharacteristicNotification(char, enabled) ?: run {
                    Log.w(TAG, "BluetoothAdapter not initialized")
                    return
                }
            Log.d(TAG, "success set notification?: $isSuccess")
        } ?: run {
            Log.w(TAG, "no characteristic that uuid are: $uuid")
        }
    }

    private fun getSupportedGattServices(): List<BluetoothGattService?>? {
        return bluetoothGatt?.services
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun close() {
        bluetoothGatt?.close()
    }


    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        Log.d(TAG, "on bind")
        return binder
    }

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(appContainer: AppContainer): BLEService {
            val service = this@BLEService
            service.appContainer = appContainer
            return service
        }
    }
}


fun BluetoothGattCharacteristic.isWritable() =
    properties and (BluetoothGattCharacteristic.PROPERTY_WRITE or BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0

fun BluetoothGattCharacteristic.isReadable() =
    properties and BluetoothGattCharacteristic.PROPERTY_READ != 0

fun BluetoothGattCharacteristic.isNotifiable() =
    properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0

enum class BLEAction(val value: String) {
    CONNECT_DEVICE("com.github.jetjinser.tendance.ACTION_CONNECT_DEVICE"),
    START_SCAN_BLE("com.github.jetjinser.tendance.ACTION_START_SCAN_BLE"),
    STOP_SCAN_BLE("com.github.jetjinser.tendance.ACTION_STOP_SCAN_BLE"),
    READ_CHARACTERISTIC("com.github.jetjinser.tendance.ACTION_READ_CHARACTERISTIC"),
    WRITE_CHARACTERISTIC("com.github.jetjinser.tendance.ACTION_WRITE_CHARACTERISTIC"),
    SET_CHARACTERISTIC_NOTIFICATION("com.github.jetjinser.tendance.ACTION_SET_CHARACTERISTIC_NOTIFICATION")
}

@Parcelize
data class WritePackage(
    val uuid: UUID,
    val value: ByteArray,
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WritePackage

        if (uuid != other.uuid) return false
        return value.contentEquals(other.value)
    }

    override fun hashCode(): Int {
        var result = uuid.hashCode()
        result = 31 * result + value.contentHashCode()
        return result
    }
}

@Parcelize
data class SetNotificationPackage(
    val uuid: UUID,
    val enable: Boolean,
) : Parcelable