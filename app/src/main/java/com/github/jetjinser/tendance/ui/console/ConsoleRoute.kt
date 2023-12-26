package com.github.jetjinser.tendance.ui.console

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ConsoleRoute(
    consoleViewModel: ConsoleViewModel,
    openDrawer: () -> Unit,
) {
    val lazyListState = rememberLazyListState()

    val services by consoleViewModel.supportedServices.collectAsStateWithLifecycle()
    val uiState by consoleViewModel.uiState.collectAsStateWithLifecycle()

    ConsoleScreen(
        services = services,
        lazyListState = lazyListState,

        onReadClicked = { consoleViewModel.readCharacteristic(it.uuid) },
        onWriteClicked = {},
        onNotificationClicked = { characteristic ->
            val enable = uiState.enabledNotificationService.contains(characteristic.uuid)
            consoleViewModel.setCharacteristicNotification(characteristic.uuid, enable)
        },

        onWriteValue = { characteristic, value ->
            consoleViewModel.writeCharacteristic(
                characteristic.uuid,
                value
            )
        },

        openDrawer = openDrawer,
    )
}