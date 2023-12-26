package com.github.jetjinser.tendance.ui.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jetjinser.tendance.data.DEMO_DEVICES
import com.github.jetjinser.tendance.data.Device

@Composable
fun DeviceList(
    devices: Set<Device>,
    onDeviceClicked: (Device) -> Unit,
    deviceLazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        state = deviceLazyListState,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        devices.forEach { device ->
            item {
                DeviceCard(device, onDeviceClicked)
            }
        }
    }
}

@Composable
fun DeviceCard(
    device: Device, onDeviceClicked: (Device) -> Unit, modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .fillMaxWidth(0.9f),
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = modifier
                    .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
            ) {
                Text(text = device.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = device.address, style = MaterialTheme.typography.labelMedium)
            }
            Button(
                modifier = modifier
                    .padding(end = 20.dp),
                onClick = {
                    onDeviceClicked(device)
                }) {
                Text(text = "Connect")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceListPreview() {
    val lazyListState = rememberLazyListState()

    DeviceList(
        devices = DEMO_DEVICES,
        onDeviceClicked = {},
        deviceLazyListState = lazyListState
    )
}