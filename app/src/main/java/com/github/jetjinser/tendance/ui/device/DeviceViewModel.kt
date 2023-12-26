package com.github.jetjinser.tendance.ui.device

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.jetjinser.tendance.data.devices.DevicesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class DeviceViewModel(
    private val devicesRepository: DevicesRepository
) : ViewModel() {
    companion object {
        private val TAG = DeviceViewModel::class.simpleName

        fun provideFactory(devicesRepository: DevicesRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return DeviceViewModel(devicesRepository) as T
                }
            }
    }

    private val _uiState = MutableStateFlow(DeviceUiState())
    val uiState: StateFlow<DeviceUiState> = _uiState.asStateFlow()

    val devices = devicesRepository.observeDevices().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptySet()
    )

    fun toggleScanning() {
        _uiState.update {
            if (it.scanning) {
                stopScanLe()
            } else {
                startScanLe()
            }

            Log.d(TAG, "scanning? ${it.scanning}")
            it.copy(
                scanning = !it.scanning
            ).also { state ->
                Log.d(TAG, "scanning? ${state.scanning}")
            }
        }
    }

    fun connectDevice(address: String) {
        devicesRepository.connectDevice(address)
    }

    private fun startScanLe() {
        devicesRepository.startScanLe()
    }

    private fun stopScanLe() {
        devicesRepository.stopScanLe()
    }
}

data class DeviceUiState(
    val scanning: Boolean = false,
)