package com.github.jetjinser.tendance.ui

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.jetjinser.tendance.ui.nav.TendanceDestinations
import com.github.jetjinser.tendance.ui.theme.TendanceTheme

@Composable
fun AppDrawer(
    currentRoute: String,
    navigateToDevices: () -> Unit,
    navigateToConsole: () -> Unit,
    navigateToImages: () -> Unit,
    closeDrawer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalDrawerSheet(modifier) {
        TendanceLogo(
            modifier = modifier.padding(horizontal = 28.dp, vertical = 24.dp)
        )

        NavigationDrawerItem(
            label = { Text("Devices") },
            selected = currentRoute == TendanceDestinations.DEVICES_ROUTE,
            onClick = { navigateToDevices(); closeDrawer() },
            modifier = modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
        NavigationDrawerItem(
            label = { Text("Console") },
            selected = currentRoute == TendanceDestinations.CONSOLE_ROUTE,
            onClick = { navigateToConsole(); closeDrawer() },
            modifier = modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
        NavigationDrawerItem(
            label = { Text("Images") },
            selected = currentRoute == TendanceDestinations.IMAGES_ROUTE,
            onClick = { navigateToImages(); closeDrawer() },
            modifier = modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
    }
}

@Composable
private fun TendanceLogo(modifier: Modifier = Modifier) {
    Text(text = "Tendance", style = MaterialTheme.typography.headlineLarge, modifier = modifier)
}

@Preview("Drawer contents")
@Preview("Drawer contents (dark)", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppDrawer() {
    TendanceTheme {
        AppDrawer(
            currentRoute = TendanceDestinations.DEVICES_ROUTE,
            navigateToDevices = {},
            navigateToConsole = {},
            navigateToImages = {},
            closeDrawer = {},
        )
    }
}