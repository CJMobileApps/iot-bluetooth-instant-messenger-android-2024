package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.R
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.IotBluetoothInstantMessengerTopAppBar
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.permissions.getBluetoothMultiplePermissionsState
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.permissions.getBluetoothPermission
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.viewmodel.ScanBluetoothViewModel
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.viewmodel.ScanBluetoothViewModelImpl
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun ScanBluetoothUi(
    navController: NavController,
    scanBluetoothViewModel: ScanBluetoothViewModel,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            IotBluetoothInstantMessengerTopAppBar(
                navController,
                topBarTitle = stringResource(R.string.scan_bluetooth_devices),
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Box {
            when (val state = scanBluetoothViewModel.getState()) {
                is ScanBluetoothViewModelImpl.ScanBluetoothState.ScanBluetoothLoadedState -> {
                    ScanBluetoothLoadedUi(
                        modifier = Modifier.padding(innerPadding),
                        scanBluetoothViewModel = scanBluetoothViewModel,
                        navController = navController,
                        scanBluetoothState = state,
                        context = context,
                    )

                    ScanBluetoothPermissionsUi(
                        context = context,
                        scanBluetoothState = state,
                        scanBluetoothViewModel = scanBluetoothViewModel,
                        coroutineScope = coroutineScope,
                    )
                }
            }
        }

        val snackbarMessage: String? =
            when (val state = scanBluetoothViewModel.getSnackbarState()) {
                is ScanBluetoothViewModelImpl.ScanBluetoothSnackbarState.Idle -> null

                is ScanBluetoothViewModelImpl.ScanBluetoothSnackbarState.ShowGenericError ->
                    state.error
                        ?: stringResource(R.string.some_error_occurred)
            }

        if (snackbarMessage != null) {
            ScanBluetoothSnackbar(
                message = snackbarMessage,
                coroutineScope = coroutineScope,
                snackbarHostState = snackbarHostState,
                scanBluetoothViewModel = scanBluetoothViewModel
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanBluetoothPermissionsUi(
    context: Context,
    scanBluetoothState: ScanBluetoothViewModelImpl.ScanBluetoothState.ScanBluetoothLoadedState,
    scanBluetoothViewModel: ScanBluetoothViewModel,
    coroutineScope: CoroutineScope,
) {
    val tag = "HomeBluetoothPermissionsUi"
    val bluetoothPermissionsState = getBluetoothMultiplePermissionsState()

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            Timber.d(tag, "Bluetooth enabled")
            Toast.makeText(context, "Bluetooth Enabled", Toast.LENGTH_SHORT).show()
            scanBluetoothViewModel.checkBluetoothPermissions()
        } else {
            Timber.e(tag, "Bluetooth enabling failed or canceled")
            Toast.makeText(context, "Bluetooth Enabled Failed", Toast.LENGTH_SHORT).show()
        }
    }

    if (scanBluetoothState.checkBluetoothPermissions.value) {
        LaunchedEffect(
            key1 = bluetoothPermissionsState.allPermissionsGranted,
            key2 = bluetoothPermissionsState.shouldShowRationale
        ) {
            getBluetoothPermission(
                onBluetoothGranted = { bluetoothAdapter ->
                    scanBluetoothViewModel.enableBluetooth(bluetoothAdapter)
                    scanBluetoothViewModel.resetCheckBluetoothPermissions()
                },
                onBluetoothDenied = { error ->
                    scanBluetoothViewModel.showBluetoothErrorSnackbar(error)
                    scanBluetoothViewModel.resetCheckBluetoothPermissions()
                },
                onEnableBluetooth = {
                    val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    enableBluetoothLauncher.launch(enableBtIntent)
                    scanBluetoothViewModel.resetCheckBluetoothPermissions()
                },
                coroutineScope = coroutineScope,
                bluetoothPermissionsState = bluetoothPermissionsState,
            )
        }
    }
}

@Composable
fun ScanBluetoothSnackbar(
    message: String,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    scanBluetoothViewModel: ScanBluetoothViewModel,
) {
    LaunchedEffect(key1 = message) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message = message)
            scanBluetoothViewModel.resetSnackbarState()
        }
    }
}

@Composable
fun ScanBluetoothLoadedUi(
    modifier: Modifier,
    scanBluetoothViewModel: ScanBluetoothViewModel,
    navController: NavController,
    scanBluetoothState: ScanBluetoothViewModelImpl.ScanBluetoothState.ScanBluetoothLoadedState,
    context: Context,
) {
    val tag = "ScanBluetoothLoadedUi"
    val bluetoothAdapter = scanBluetoothViewModel.getBluetoothAdapter()
    if (scanBluetoothViewModel.isBluetoothEnabled() && bluetoothAdapter != null) {
        Timber.e(tag, "Bluetooth enabled")
        ScanUi(
            modifier = modifier,
            bluetoothAdapter = bluetoothAdapter,
            scanBluetoothState = scanBluetoothState,
            context = context,
        )
    } else {
        Timber.e(tag, "Bluetooth not enabled")
    }

    when (val navigateRouteUiValue = scanBluetoothViewModel.getScanBluetoothNavRouteUiState()) {
        is ScanBluetoothViewModelImpl.ScanBluetoothNavRouteUi.Idle -> {}
        is ScanBluetoothViewModelImpl.ScanBluetoothNavRouteUi.GoToChatUi -> {
            navController.navigate(navigateRouteUiValue.getNavRoute())
            scanBluetoothViewModel.resetNavRouteUiToIdle()
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun ScanUi(
    modifier: Modifier,
    bluetoothAdapter: BluetoothAdapter,
    scanBluetoothState: ScanBluetoothViewModelImpl.ScanBluetoothState.ScanBluetoothLoadedState,
    context: Context,
) {
    val tag = "ScanUi"
    val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter.bluetoothLeScanner

    val foundDevices = remember { mutableStateListOf<BluetoothDevice>() }

    val scanCallback = remember {
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                if (!foundDevices.contains(device) && device.name != null) {
                    foundDevices.add(device)
                    Timber.d(tag, " BLE Found device: ${device.name}")
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Timber.d(tag, " BLE Scan failed with error: $errorCode")
            }
        }
    }

    if (scanBluetoothState.isScanning.value) {
        if (bluetoothLeScanner == null) {
            Toast.makeText(context, "BLE Scanner not available", Toast.LENGTH_SHORT).show()
            return
        }
        foundDevices.clear()
        bluetoothLeScanner.startScan(scanCallback)
        Toast.makeText(context, "Scanning...", Toast.LENGTH_SHORT).show()
    } else {
        bluetoothLeScanner?.stopScan(scanCallback)
        Toast.makeText(context, "Scan Stopped", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 32.dp, end = 32.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(if (scanBluetoothState.isScanning.value) "Scanning..." else "Done Scanning")
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(foundDevices) { device ->
                DeviceItem(device, context)
            }
        }
    }
}

//TODO use a BluetoothDevice ui model
@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(device: BluetoothDevice, context: Context) {
    ElevatedCard(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable {
                Toast
                    .makeText(context, "BLE  Clicked on ${device.name}", Toast.LENGTH_SHORT)
                    .show()
                Log.d("BLE", "Clicked on ${device.name}")
            },
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Device: ${device.name ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(text = "Address: ${device.address}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
