package com.github.jetjinser.tendance.ui.console

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.jetjinser.tendance.data.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsoleScreen(
    services: Map<Device, BluetoothGattService>,
    lazyListState: LazyListState,

    onReadClicked: (BluetoothGattCharacteristic) -> Unit,
    onWriteClicked: (BluetoothGattCharacteristic) -> Unit,
    onNotificationClicked: (BluetoothGattCharacteristic) -> Unit,

    onWriteValue: (BluetoothGattCharacteristic, ByteArray) -> Unit,

    openDrawer: () -> Unit,
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Console",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "nav",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            Toast.makeText(
                                context,
                                "Search is not yet implemented in this configuration",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "search"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            ServiceList(
                services = services,
                onReadClicked = onReadClicked,
                onWriteClicked = onWriteClicked,
                onNotificationClicked = onNotificationClicked,
                onWriteValue = onWriteValue,
                serviceLazyListState = lazyListState,
            )
        }
    }
}