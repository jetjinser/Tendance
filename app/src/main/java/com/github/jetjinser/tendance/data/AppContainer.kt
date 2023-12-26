package com.github.jetjinser.tendance.data

import android.content.Context
import com.github.jetjinser.tendance.data.console.ConsoleRepository
import com.github.jetjinser.tendance.data.console.ConsoleRepositoryImpl
import com.github.jetjinser.tendance.data.devices.DevicesRepository
import com.github.jetjinser.tendance.data.devices.DevicesRepositoryImpl

interface AppContainer {
    val devicesRepository: DevicesRepository
    val consoleRepository: ConsoleRepository
}

class AppContainerImpl(
    private val applicationContext: Context,
) : AppContainer {
    override val devicesRepository: DevicesRepository by lazy {
        DevicesRepositoryImpl(applicationContext)
    }

    override val consoleRepository: ConsoleRepository by lazy {
        ConsoleRepositoryImpl(applicationContext)
    }
}