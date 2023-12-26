package com.github.jetjinser.tendance

import android.app.Application
import com.github.jetjinser.tendance.data.AppContainer
import com.github.jetjinser.tendance.data.AppContainerImpl

class TendanceApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}