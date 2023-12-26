package com.github.jetjinser.tendance.data.devices

import android.content.Intent
import com.github.jetjinser.tendance.data.Device
import com.github.jetjinser.tendance.service.ble.BLEAction
import com.github.jetjinser.tendance.service.ble.BLEService
import kotlinx.coroutines.flow.Flow

abstract class DevicesRepository {
    abstract fun observeDevices(): Flow<Set<Device>>
    abstract fun addDevice(device: Device)

    abstract fun sendBroadcast(intent: Intent)

    fun connectDevice(address: String) {
        Intent(BLEAction.CONNECT_DEVICE.value).also {
            it.putExtra(BLEService.EXTRA_DATA, address)
            sendBroadcast(it)
        }
    }

    fun startScanLe() {
        Intent(BLEAction.START_SCAN_BLE.value).also {
            sendBroadcast(it)
        }
    }

    fun stopScanLe() {
        Intent(BLEAction.STOP_SCAN_BLE.value).also {
            sendBroadcast(it)
        }
    }

}