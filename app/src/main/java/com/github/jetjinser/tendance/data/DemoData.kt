package com.github.jetjinser.tendance.data

import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_SECONDARY
import java.util.UUID

val DEMO_DEVICES = setOf(
    Device("esp32-c3", "abc"),
    Device("mbp intel", "apple"),
    Device("redmi 13p", "redmi"),
    Device("Unknown", "xxo-bba"),
)