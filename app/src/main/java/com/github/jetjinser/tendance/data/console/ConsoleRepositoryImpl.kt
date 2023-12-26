package com.github.jetjinser.tendance.data.console

import android.bluetooth.BluetoothGattService
import android.content.Context
import android.content.Intent
import com.github.jetjinser.tendance.data.Device
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ConsoleRepositoryImpl(
    private val applicationContext: Context
) : ConsoleRepository() {
    private val services = MutableStateFlow(emptyMap<Device, BluetoothGattService>())

    override fun observeSupportedServices(): Flow<Map<Device, BluetoothGattService>> =
        services

    override fun addService(device: Device, service: BluetoothGattService) {
        services.update { map ->
            map + mapOf(device to service)
        }
    }

    override fun sendBroadcast(intent: Intent) {
        applicationContext.sendBroadcast(intent)
    }
}