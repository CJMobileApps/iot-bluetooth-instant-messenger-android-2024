package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.HomeUi
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel.HomeViewModel
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel.HomeViewModelImpl
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.ScanBluetoothUi
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.viewmodel.ScanBluetoothViewModel
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.viewmodel.ScanBluetoothViewModelImpl
import kotlinx.coroutines.CoroutineScope

@Composable
fun NavigationGraph(
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    NavHost(navController = navController, startDestination = NavItem.Home.navRoute) {
        composable(NavItem.Home.navRoute) {
            val homeViewModel: HomeViewModel = hiltViewModel<HomeViewModelImpl>()

            HomeUi(
                navController = navController,
                homeViewModel = homeViewModel,
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState,
            )
        }

        composable(NavItem.ScanBluetooth.navRoute) {
            val scanBluetoothViewModel: ScanBluetoothViewModel = hiltViewModel<ScanBluetoothViewModelImpl>()

            ScanBluetoothUi(
                navController = navController,
                scanBluetoothViewModel = scanBluetoothViewModel,
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState,
            )
        }
    }
}

sealed class NavItem(
    val navRoute: String,
    val arguments: List<NamedNavArgument> = emptyList(),
) {
    data object Home : NavItem(navRoute = "nav_home")

    data object ScanBluetooth : NavItem(navRoute = "nav_scan_bluetooth") {
        fun getNavRouteWithArguments(): String {
            return "nav_scan_bluetooth"
        }
    }
}
