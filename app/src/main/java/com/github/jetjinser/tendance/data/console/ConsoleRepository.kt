package com.github.jetjinser.tendance.data.console

import android.bluetooth.BluetoothGattService
import android.content.Intent
import com.github.jetjinser.tendance.data.Device
import com.github.jetjinser.tendance.service.ble.BLEAction
import com.github.jetjinser.tendance.service.ble.BLEService
import com.github.jetjinser.tendance.service.ble.SetNotificationPackage
import com.github.jetjinser.tendance.service.ble.WritePackage
import kotlinx.coroutines.flow.Flow
import java.util.UUID

abstract class ConsoleRepository {
    abstract fun observeSupportedServices(): Flow<Map<Device, BluetoothGattService>>
    abstract fun addService(device: Device, service: BluetoothGattService)

    abstract fun sendBroadcast(intent: Intent)

    fun readCharacteristic(uuid: UUID) {
        Intent(BLEAction.READ_CHARACTERISTIC.value).also {
            it.putExtra(BLEService.EXTRA_DATA, uuid)
            sendBroadcast(it)
        }
    }

    fun writeCharacteristic(uuid: UUID, value: ByteArray) {
        Intent(BLEAction.WRITE_CHARACTERISTIC.value).also {
            val writePackage = WritePackage(uuid, value)
            it.putExtra(BLEService.EXTRA_DATA, writePackage)
            sendBroadcast(it)
        }
    }

    fun setCharacteristicNotification(
        uuid: UUID,
        enable: Boolean
    ) {
        Intent(BLEAction.SET_CHARACTERISTIC_NOTIFICATION.value).also {
            val setNotificationPackage = SetNotificationPackage(uuid, enable)
            it.putExtra(BLEService.EXTRA_DATA, setNotificationPackage)
            sendBroadcast(it)
        }
    }
}