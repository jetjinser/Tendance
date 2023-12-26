package com.github.jetjinser.tendance.ui.device

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun ScanFloatingActionButton(
    scanning: Boolean, onScanClicked: () -> Unit, modifier: Modifier = Modifier
) {
    LaunchedEffect(scanning) {
        if (scanning) {
            delay(7.seconds)
            onScanClicked()
        }
    }

    FloatingActionButton(
        modifier = modifier, onClick = onScanClicked
    ) {
        if (scanning) {
            Icon(Icons.Default.Close, contentDescription = "Stop")
        } else {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
        }
    }
}
