package com.github.jetjinser.tendance.ui.nav

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination

class TendanceNavigation(private val navController: NavController) {
    private fun navigateTo(to: String) {
        navController.navigate(to) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    val navigateToDevices: () -> Unit = { navigateTo(TendanceDestinations.DEVICES_ROUTE) }
    val navigateToConsole: () -> Unit = { navigateTo(TendanceDestinations.CONSOLE_ROUTE) }
    val navigateToImages: () -> Unit = { navigateTo(TendanceDestinations.IMAGES_ROUTE) }
}