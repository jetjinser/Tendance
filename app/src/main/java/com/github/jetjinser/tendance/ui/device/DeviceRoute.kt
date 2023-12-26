package com.github.jetjinser.tendance.ui.device

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DeviceRoute(
    deviceViewModel: DeviceViewModel,
    openDrawer: () -> Unit,
) {
    val deviceLazyListState = rememberLazyListState()

    val deviceSection = rememberDeviceSection(viewModel = deviceViewModel, deviceLazyListState = deviceLazyListState)
    val uiState by deviceViewModel.uiState.collectAsStateWithLifecycle()

    DeviceScreen(
        uiState = uiState,
        deviceSection = deviceSection,
        openDrawer = openDrawer,
        onScanClicked = {
            deviceViewModel.toggleScanning()
        },
    )
}