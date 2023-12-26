package com.github.jetjinser.tendance.data.devices

import android.content.Context
import android.content.Intent
import com.github.jetjinser.tendance.data.Device
import com.github.jetjinser.tendance.utils.mutableAdd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class DevicesRepositoryImpl(private val applicationContext: Context) : DevicesRepository() {
    private val devices = MutableStateFlow(setOf<Device>())

    override fun observeDevices(): Flow<Set<Device>> = devices

    override fun addDevice(device: Device) {
        devices.update {
            it.mutableAdd(device)
        }
    }

    override fun sendBroadcast(intent: Intent) {
        applicationContext.sendBroadcast(intent)
    }
}