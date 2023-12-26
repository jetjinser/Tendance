package com.github.jetjinser.tendance.ui.device

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle

data class DeviceSection(val content: @Composable () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
    uiState: DeviceUiState,
    deviceSection: DeviceSection,
    openDrawer: () -> Unit,
    onScanClicked: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = "Tendance", style = MaterialTheme.typography.titleLarge
                )
            }, navigationIcon = {
                IconButton(onClick = openDrawer) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "nav",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }, actions = {
                IconButton(onClick = {
                    Toast.makeText(
                        context,
                        "Search is not yet implemented in this configuration",
                        Toast.LENGTH_LONG
                    ).show()
                }) {
                    Icon(
                        imageVector = Icons.Filled.Search, contentDescription = "search"
                    )
                }
            })
        },
        floatingActionButton = {
            ScanFloatingActionButton(
                scanning = uiState.scanning, onScanClicked = onScanClicked
            )
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            deviceSection.content()
        }
    }
}

@Composable
fun rememberDeviceSection(
    viewModel: DeviceViewModel, deviceLazyListState: LazyListState
): DeviceSection {
    val devices by viewModel.devices.collectAsStateWithLifecycle()

    return DeviceSection {
        DeviceList(
            devices = devices, onDeviceClicked = { device ->
                viewModel.connectDevice(device.address)
            }, deviceLazyListState = deviceLazyListState
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FindScreenPreview() {
    TODO()
}