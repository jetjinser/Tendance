package com.github.jetjinser.tendance.ui

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.jetjinser.tendance.data.AppContainer
import com.github.jetjinser.tendance.ui.nav.TendanceDestinations
import com.github.jetjinser.tendance.ui.nav.TendanceNavGraph
import com.github.jetjinser.tendance.ui.nav.TendanceNavigation
import com.github.jetjinser.tendance.ui.theme.TendanceTheme
import kotlinx.coroutines.launch

@Composable
fun TendanceApp(
    appContainer: AppContainer,
    widthSizeClass: WindowWidthSizeClass,
) {
    TendanceTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            TendanceNavigation(navController)
        }

        val coroutineScope = rememberCoroutineScope()

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute =
            navBackStackEntry?.destination?.route ?: TendanceDestinations.DEVICES_ROUTE

        val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded
        val sizeAwareDrawerState = rememberSizeAwareDrawerState(isExpandedScreen)

        ModalNavigationDrawer(
            drawerContent = {
                AppDrawer(
                    currentRoute = currentRoute,
                    navigateToDevices = navigationActions.navigateToDevices,
                    navigateToConsole = navigationActions.navigateToConsole,
                    navigateToImages = navigationActions.navigateToImages,
                    closeDrawer = { coroutineScope.launch { sizeAwareDrawerState.close() } }
                )
            },
            drawerState = sizeAwareDrawerState,
            gesturesEnabled = !isExpandedScreen,
        ) {
            TendanceNavGraph(
                appContainer = appContainer,
                navController = navController,
                openDrawer = { coroutineScope.launch { sizeAwareDrawerState.open() } }
            )
        }
    }
}


@Composable
private fun rememberSizeAwareDrawerState(isExpandedScreen: Boolean): DrawerState {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    return if (!isExpandedScreen) {
        drawerState
    } else {
        DrawerState(DrawerValue.Closed)
    }
}

@Preview(showBackground = true)
@Composable
fun TendanceAppPreview() {
}