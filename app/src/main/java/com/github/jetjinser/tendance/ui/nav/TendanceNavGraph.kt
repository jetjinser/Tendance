package com.github.jetjinser.tendance.ui.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.jetjinser.tendance.data.AppContainer
import com.github.jetjinser.tendance.ui.console.ConsoleRoute
import com.github.jetjinser.tendance.ui.console.ConsoleViewModel
import com.github.jetjinser.tendance.ui.device.DeviceRoute
import com.github.jetjinser.tendance.ui.image.ImageRoute
import com.github.jetjinser.tendance.ui.device.DeviceViewModel

@Composable
fun TendanceNavGraph(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    openDrawer: () -> Unit = {},
    startDestination: String = TendanceDestinations.DEVICES_ROUTE,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable(TendanceDestinations.DEVICES_ROUTE) {
            val deviceViewModel: DeviceViewModel = viewModel(
                factory = DeviceViewModel.provideFactory(appContainer.devicesRepository)
            )
            DeviceRoute(
                deviceViewModel = deviceViewModel,
                openDrawer = openDrawer,
            )
        }
        composable(TendanceDestinations.CONSOLE_ROUTE) {
            val consoleViewModel: ConsoleViewModel = viewModel(
                factory = ConsoleViewModel.provideFactory(appContainer.consoleRepository)
            )
            ConsoleRoute(
                consoleViewModel = consoleViewModel,
                openDrawer = openDrawer,
            )
        }
        composable(TendanceDestinations.IMAGES_ROUTE) {
            ImageRoute(
                openDrawer = openDrawer,
            )
        }
    }
}