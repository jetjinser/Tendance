package com.github.jetjinser.tendance.ui.console

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.github.jetjinser.tendance.data.Device
import com.github.jetjinser.tendance.service.ble.GattAttributes
import com.github.jetjinser.tendance.service.ble.isNotifiable
import com.github.jetjinser.tendance.service.ble.isReadable
import com.github.jetjinser.tendance.service.ble.isWritable

@Composable
fun ServiceList(
    services: Map<Device, BluetoothGattService>,

    onReadClicked: (BluetoothGattCharacteristic) -> Unit,
    onWriteClicked: (BluetoothGattCharacteristic) -> Unit,
    onNotificationClicked: (BluetoothGattCharacteristic) -> Unit,

    onWriteValue: (BluetoothGattCharacteristic, ByteArray) -> Unit,

    serviceLazyListState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp), state = serviceLazyListState
    ) {
        services.forEach {
            items(items = it.value.characteristics) { characteristic ->
                CharacteristicCard(
                    characteristic,

                    onReadClicked = onReadClicked,
                    onWriteClicked = onWriteClicked,
                    onNotificationClick = onNotificationClicked,

                    onWriteValue = onWriteValue,
                )
            }
        }
    }
}

@Composable
fun CharacteristicCard(
    characteristic: BluetoothGattCharacteristic,

    onReadClicked: (BluetoothGattCharacteristic) -> Unit,
    onWriteClicked: (BluetoothGattCharacteristic) -> Unit,
    onNotificationClick: (BluetoothGattCharacteristic) -> Unit,

    onWriteValue: (BluetoothGattCharacteristic, ByteArray) -> Unit,

    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f, label = "expand animation"
    )

    ElevatedCard(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 500, easing = LinearOutSlowInEasing
                )
            )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row {
                Column {
                    Text(
                        text = GattAttributes.lookup(
                            characteristic.uuid.toString(), "Unknown Service"
                        ), style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = characteristic.uuid.toString(),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .alpha(0.2f)
                        .rotate(rotationState),
                    onClick = {
                        expanded = !expanded
                    }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }

            if (expanded) {
                PropertiesChips(
                    characteristic = characteristic,
                    onReadClicked = onReadClicked,
                    onWriteClicked = onWriteClicked,
                    onNotificationClick = onNotificationClick,
                    onWriteValue = onWriteValue,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertiesChips(
    characteristic: BluetoothGattCharacteristic,

    onReadClicked: (BluetoothGattCharacteristic) -> Unit,
    onWriteClicked: (BluetoothGattCharacteristic) -> Unit,
    onNotificationClick: (BluetoothGattCharacteristic) -> Unit,

    onWriteValue: (BluetoothGattCharacteristic, ByteArray) -> Unit,

    modifier: Modifier = Modifier,
) {
    var value by remember {
        mutableStateOf("")
    }
    var popup by remember {
        mutableStateOf(false)
    }

    val isReadable = characteristic.isReadable()
    val isWritable = characteristic.isWritable()
    val isNotifiable = characteristic.isNotifiable()

    val readable = if (isReadable) "readable" else "unreadable"
    val writable = if (isWritable) "writable" else "unwritable"
    val notifiable = if (isNotifiable) "notifiable" else "unnotifiable"

    Row(
        modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SuggestionChip(enabled = isReadable, onClick = {
            onReadClicked(characteristic)
        }, label = {
            Text(text = readable)
        })
        SuggestionChip(enabled = isWritable, onClick = {
            onWriteClicked(characteristic)
            popup = true
        }, label = {
            Text(text = writable)
            if (popup) {
                Dialog(onDismissRequest = {
                    value = ""
                    popup = false
                }) {
                    Card {
                        OutlinedTextField(modifier = modifier.padding(5.dp),
                            value = value,
                            label = {
                                Text(text = "value to send")
                            },
                            onValueChange = {
                                value = it
                            })

                        Row(
                            modifier = modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                value = ""
                                popup = false
                            }) {
                                Text(text = "Cancel")
                            }
                            TextButton(onClick = {
                                Log.d("SND", "sending $value to ${characteristic.uuid}")
                                onWriteValue(characteristic, value.toByteArray())
                                value = ""
                                popup = false
                            }) {
                                Text(text = "Send")
                            }
                        }
                    }
                }
            }
        })
        FilterChip(selected = false, enabled = isNotifiable, onClick = {
            onNotificationClick(characteristic)
        }, label = {
            Text(text = notifiable)
        })
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceListPreview() {
    val lazyListState = rememberLazyListState()

    ServiceList(
        services = emptyMap(),

        onReadClicked = {}, onWriteClicked = {}, onNotificationClicked = {},

        onWriteValue = { _, _ -> run {} },

        serviceLazyListState = lazyListState
    )
}
