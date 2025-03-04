package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@Composable
fun getActivity(): Activity? {
    val context = LocalContext.current
    return context as? Activity
}

//TODO check/request permissions then do auto scan
//TODO if not enabled tell them
@Composable
fun ScanBluetoothUi(
    navController: NavController,
) {
    Scaffold(
        //snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .padding(top = 16.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("ScanBluetooth Screen")
//            val activity = getActivity()
//            setupBluetooth(activity = activity)
            BluetoothScreen()
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun BluetoothScreen() {
    val context = LocalContext.current
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner

    var isScanning by remember { mutableStateOf(false) }
    val foundDevices = remember { mutableStateListOf<BluetoothDevice>() }

    val scanCallback = remember {
        object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device
                if (!foundDevices.contains(device) && device.name != null) {
                    foundDevices.add(device)
                    Log.d("BLE", "Found device: ${device.name}")
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("BLE", "Scan failed with error: $errorCode")
            }
        }
    }

    // Start Bluetooth scan
    @SuppressLint("MissingPermission")
    fun startScan() {
        if (bluetoothLeScanner == null) {
            Toast.makeText(context, "BLE Scanner not available", Toast.LENGTH_SHORT).show()
            return
        }
        foundDevices.clear()
        bluetoothLeScanner.startScan(scanCallback)
        isScanning = true
        Toast.makeText(context, "Scanning...", Toast.LENGTH_SHORT).show()
    }

    // Stop Bluetooth scan
    @SuppressLint("MissingPermission")
    fun stopScan() {
        bluetoothLeScanner?.stopScan(scanCallback)
        isScanning = false
        Toast.makeText(context, "Scan Stopped", Toast.LENGTH_SHORT).show()
    }

    // Layout UI
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (isScanning) {
                stopScan()
            } else {
                startScan()
            }
        }) {
            Text(if (isScanning) "Stop Scan" else "Start Scan")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(foundDevices) { device ->
                DeviceItem(device, context)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
fun DeviceItem(device: BluetoothDevice, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                Toast
                    .makeText(context, "BLE  Clicked on ${device.name}", Toast.LENGTH_SHORT)
                    .show()
                Log.d("BLE", "Clicked on ${device.name}")

            },
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

