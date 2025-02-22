package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.R
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.IotBluetoothInstantMessengerTopAppBar
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel.HomeViewModel
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel.HomeViewModelImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeUi(
    navController: NavController,
    homeViewModel: HomeViewModel,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    Scaffold(
        topBar = { IotBluetoothInstantMessengerTopAppBar(navController) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box {
            when (val state = homeViewModel.getState()) {
                is HomeViewModelImpl.HomeState.HomeLoadedState -> {
                    HomeLoadedUi(
                        modifier = Modifier.padding(innerPadding),
                        homeViewModel = homeViewModel,
                        homeLoadedState = state,
                        navController = navController,
                    )
                }
            }
        }

        val snackbarMessage: String? =
            when (val state = homeViewModel.getSnackbarState()) {
                is HomeViewModelImpl.HomeSnackbarState.Idle -> null

                is HomeViewModelImpl.HomeSnackbarState.ShowGenericError ->
                    state.error
                        ?: stringResource(R.string.some_error_occurred)
            }

        if (snackbarMessage != null) {
            HomeSnackbar(
                message = snackbarMessage,
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState,
                homeViewModel = homeViewModel,
            )
        }
    }
}

@Composable
fun HomeSnackbar(
    message: String,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    homeViewModel: HomeViewModel,
) {
    LaunchedEffect(key1 = message) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message = message)
            homeViewModel.resetSnackbarState()
        }
    }
}

@Composable
fun HomeLoadedUi(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
    homeLoadedState: HomeViewModelImpl.HomeState.HomeLoadedState,
    navController: NavController,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // TODO make this button more square and add bluetooth Icon
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {  }) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = "Bluetooth"
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("Scan for devices")
        }
    }

    //todo go to blank connected chat screen
//    when (val navigateRouteUiValue = housesViewModel.getHousesNavRouteUiState()) {
//        is HousesViewModelImpl.HousesNavRouteUi.Idle -> {}
//        is HousesViewModelImpl.HousesNavRouteUi.GoToPlayerListUi -> {
//            navController.navigate(navigateRouteUiValue.getNavRouteWithArguments())
//            housesViewModel.resetNavRouteUiToIdle()
//        }
//    }
}

// TODO add android previews
