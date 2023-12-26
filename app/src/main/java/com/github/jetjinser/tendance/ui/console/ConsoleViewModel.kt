package com.github.jetjinser.tendance.ui.console

import android.bluetooth.BluetoothGattService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.github.jetjinser.tendance.data.Device
import com.github.jetjinser.tendance.data.console.ConsoleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

class ConsoleViewModel(
    private val consoleRepository: ConsoleRepository,
) : ViewModel() {
    companion object {
        fun provideFactory(consoleRepository: ConsoleRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return ConsoleViewModel(consoleRepository) as T
                }
            }
    }


    private val _uiState = MutableStateFlow(ConsoleUiState())
    val uiState: StateFlow<ConsoleUiState> = _uiState.asStateFlow()

    val supportedServices = consoleRepository.observeSupportedServices().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyMap()
    )

    fun readCharacteristic(uuid: UUID) {
        consoleRepository.readCharacteristic(uuid)
    }

    fun writeCharacteristic(uuid: UUID, value: ByteArray) {
        consoleRepository.writeCharacteristic(uuid, value)
    }

    fun setCharacteristicNotification(
        uuid: UUID,
        enable: Boolean
    ) {
        consoleRepository.setCharacteristicNotification(uuid, enable)
    }
}

data class ConsoleUiState(
    val supportedServices: Map<Device, BluetoothGattService> = emptyMap(),

    val enabledNotificationService: Set<UUID> = emptySet(),
)