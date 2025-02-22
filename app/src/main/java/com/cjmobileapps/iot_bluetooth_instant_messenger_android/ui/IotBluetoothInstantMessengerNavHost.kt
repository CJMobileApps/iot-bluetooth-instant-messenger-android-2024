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
import kotlinx.coroutines.CoroutineScope

@Composable
fun NavigationGraph(
    navController: NavHostController,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    NavHost(navController = navController, startDestination = NavItem.Houses.navRoute) {
        composable(NavItem.Houses.navRoute) {
            val homeViewModel: HomeViewModel = hiltViewModel<HomeViewModelImpl>()

            HomeUi(
                navController = navController,
                homeViewModel = homeViewModel,
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
    data object Houses : NavItem(navRoute = "nav_houses")
}
