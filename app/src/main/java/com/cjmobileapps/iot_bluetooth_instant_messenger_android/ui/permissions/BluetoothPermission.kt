package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.os.Build
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
fun getBluetoothPermission(
    onBluetoothGranted: () -> Unit = { },
    onBluetoothDenied: (deniedReason : String?) -> Unit = { },
    coroutineScope: CoroutineScope,
    bluetoothPermissionsState: MultiplePermissionsState,
) {
    if (bluetoothPermissionsState.allPermissionsGranted) {
        //TODO this is deprecated
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        if (bluetoothAdapter == null) {
            onBluetoothDenied.invoke("Device doesn't support Bluetooth")
        } else if (!bluetoothAdapter.isEnabled) {
            onBluetoothDenied.invoke("Bluetooth is not enabled")
        } else {
            onBluetoothGranted.invoke()
        }
    } else {
        if (bluetoothPermissionsState.shouldShowRationale)  {
            onBluetoothDenied.invoke("Bluetooth is denied. Please enable")
        } else {
            coroutineScope.launch {
                bluetoothPermissionsState.launchMultiplePermissionRequest()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun getBluetoothMultiplePermissionsState(): MultiplePermissionsState {
    val permissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION // Required for API < 31
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    } else {
        permissions.add(Manifest.permission.BLUETOOTH) // Required for API < 31
        permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
    }

   return rememberMultiplePermissionsState(permissions)
}
