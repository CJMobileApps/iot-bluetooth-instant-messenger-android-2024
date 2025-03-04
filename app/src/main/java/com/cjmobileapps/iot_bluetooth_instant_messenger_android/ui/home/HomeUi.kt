package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.R
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.IotBluetoothInstantMessengerTopAppBar
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel.HomeViewModel
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel.HomeViewModelImpl
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.permissions.getBluetoothMultiplePermissionsState
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.permissions.getBluetoothPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeUi(
    navController: NavController,
    homeViewModel: HomeViewModel,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current

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
                        coroutineScope = coroutineScope,
                        context = context,
                    )

                    HomeBluetoothPermissionsUi(
                        context = context,
                        homeLoadedState = state,
                        homeViewModel = homeViewModel,
                        coroutineScope = coroutineScope,
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeBluetoothPermissionsUi(
    context: Context,
    homeLoadedState: HomeViewModelImpl.HomeState.HomeLoadedState,
    homeViewModel: HomeViewModel,
    coroutineScope: CoroutineScope,
) {
    val bluetoothPermissionsState = getBluetoothMultiplePermissionsState()

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            Log.d("BLE", "Bluetooth enabled")
            Toast.makeText(context, "Bluetooth Enabled", Toast.LENGTH_SHORT).show()
            homeViewModel.checkBluetoothPermissions()
        } else {
            Log.e("BLE", "Bluetooth enabling failed or canceled")
            Toast.makeText(context, "Bluetooth Enable Failed", Toast.LENGTH_SHORT).show()
        }
    }

    if (homeLoadedState.checkBluetoothPermissions.value) {
        LaunchedEffect(
            key1 = bluetoothPermissionsState.allPermissionsGranted,
            key2 = bluetoothPermissionsState.shouldShowRationale
        ) {
            getBluetoothPermission(
                onBluetoothGranted = {
                    homeViewModel.goToScanBluetoothUi()
                    homeViewModel.resetCheckBluetoothPermissions()
                },
                onBluetoothDenied = { error ->
                    homeViewModel.showBluetoothErrorSnackbar(error)
                    homeViewModel.resetCheckBluetoothPermissions()
                },
                onEnableBluetooth = {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    enableBluetoothLauncher.launch(enableBtIntent)
                    homeViewModel.resetCheckBluetoothPermissions()
                },
                coroutineScope = coroutineScope,
                bluetoothPermissionsState = bluetoothPermissionsState,
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
    coroutineScope: CoroutineScope,
    context: Context,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            onClick = {
                homeViewModel.checkBluetoothPermissions()
            }) {
            Icon(
                imageVector = Icons.Default.Bluetooth,
                contentDescription = "Bluetooth"
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text("Scan for devices")
        }
    }

    when (val navigateRouteUiValue = homeViewModel.getHomeNavRouteUiState()) {
        is HomeViewModelImpl.HomeNavRouteUi.Idle -> {}
        is HomeViewModelImpl.HomeNavRouteUi.GoToScanBluetoothUi -> {
            navController.navigate(navigateRouteUiValue.getNavRoute())
            homeViewModel.resetNavRouteUiToIdle()
        }
    }
}

// TODO add android previews
