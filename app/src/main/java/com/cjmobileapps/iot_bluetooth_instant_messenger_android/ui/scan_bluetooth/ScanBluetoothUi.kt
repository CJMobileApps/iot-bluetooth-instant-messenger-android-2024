package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.R
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.model.BluetoothDeviceUiModel
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.model.toBluetoothDeviceUiModels
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.IotBluetoothInstantMessengerTopAppBar
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.permissions.getBluetoothMultiplePermissionsState
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.permissions.getBluetoothPermission
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.viewmodel.ScanBluetoothViewModel
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.viewmodel.ScanBluetoothViewModelImpl
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.theme.IotBluetoothInstantMessengerAndroid2024Theme
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
    scanBluetoothViewModel: ScanBluetoothViewModel,
    coroutineScope: CoroutineScope,
) {
    val tag = "HomeBluetoothPermissionsUi"
    val bluetoothPermissionsState = getBluetoothMultiplePermissionsState()

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Timber.tag(tag).d("Bluetooth enabled")
            Toast.makeText(context, "Bluetooth Enabled", Toast.LENGTH_SHORT).show()
            scanBluetoothViewModel.setShouldCheckForBluetoothPermissions()
        } else {
            Timber.tag(tag).e("Bluetooth enabling failed or canceled")
            Toast.makeText(context, "Bluetooth Enabled Failed", Toast.LENGTH_SHORT).show()
        }
    }

    if (scanBluetoothViewModel.checkBluetoothPermissions()) {
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
            scanBluetoothViewModel = scanBluetoothViewModel,
            context = context,
        )
    } else {
        Timber.tag(tag).e("Bluetooth not enabled")
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
    scanBluetoothViewModel: ScanBluetoothViewModel,
    context: Context,
) {
    val tag = "ScanUi"
    val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter.bluetoothLeScanner

    LaunchedEffect(scanBluetoothViewModel.isScanning()) {
        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                scanBluetoothViewModel.foundDevice(device)
            }

            override fun onScanFailed(errorCode: Int) {
                Timber.tag(tag).d("BLE Scan failed with error: $errorCode")
            }
        }

        if (scanBluetoothViewModel.isScanning()) {
            if (bluetoothLeScanner == null) {
                Toast.makeText(context, "BLE Scanner not available", Toast.LENGTH_SHORT).show()
                return@LaunchedEffect
            }
            scanBluetoothViewModel.clearAllFoundDevices()
            bluetoothLeScanner?.startScan(scanCallback)
            Toast.makeText(context, "Scanning...", Toast.LENGTH_SHORT).show()
        } else {
            bluetoothLeScanner?.stopScan(scanCallback)
            Toast.makeText(context, "Scan Stopped", Toast.LENGTH_SHORT).show()
        }
    }

    ScanDeviceItemsListUi(
        modifier = modifier,
        isScanning = scanBluetoothViewModel.isScanning(),
        foundDevicesList = scanBluetoothState.foundDevicesList.toBluetoothDeviceUiModels(),
        context = context,
    )
}

@Composable
fun ScanDeviceItemsListUi(
    modifier: Modifier = Modifier,
    isScanning: Boolean,
    foundDevicesList: List<BluetoothDeviceUiModel>,
    context: Context,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 32.dp, end = 32.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(if (isScanning) "Scanning..." else "Done Scanning")
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(foundDevicesList) { device ->
                DeviceItemUi(device, context)
            }
        }
    }
}

@PreviewLightDark
@Composable
fun ScanDeviceItemsListUiPreview() {
    val deviceUiModels = listOf(
        BluetoothDeviceUiModel(name = "CJMobileApps Phone", address = "123456"),
        BluetoothDeviceUiModel(name = "CJMobileApps Other Phone", address = "789")
    )

    val context = LocalContext.current

    IotBluetoothInstantMessengerAndroid2024Theme {
        ScanDeviceItemsListUi(
            isScanning = true,
            foundDevicesList = deviceUiModels,
            context = context,
        )
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItemUi(device: BluetoothDeviceUiModel, context: Context) {
    val tag = "DeviceItem"
    ElevatedCard(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable {
                Toast
                    .makeText(context, "BLE  Clicked on ${device.name}", Toast.LENGTH_SHORT)
                    .show()
                Timber
                    .tag(tag)
                    .d(" BLE Clicked on ${device.name}")
            },
        colors =
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Device: ${device.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(text = "Address: ${device.address}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@PreviewLightDark
@Composable
fun DeviceItemUiPreview() {
    val deviceUiModel = BluetoothDeviceUiModel(name = "CJMobileApps Phone", address = "123456")
    val context = LocalContext.current

    IotBluetoothInstantMessengerAndroid2024Theme {
        DeviceItemUi(deviceUiModel, context)
    }
}
