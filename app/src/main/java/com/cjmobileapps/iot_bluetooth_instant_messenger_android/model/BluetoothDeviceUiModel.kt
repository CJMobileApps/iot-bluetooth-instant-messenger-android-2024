package com.cjmobileapps.iot_bluetooth_instant_messenger_android.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.snapshots.SnapshotStateList

data class BluetoothDeviceUiModel(
    val name: String,
    val address: String,
)

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceUiModel(): BluetoothDeviceUiModel {
    return BluetoothDeviceUiModel(
        name = this.name,
        address = this.address
    )
}

fun SnapshotStateList<BluetoothDevice>.toBluetoothDeviceUiModels(): List<BluetoothDeviceUiModel> {
    return this.map { it.toBluetoothDeviceUiModel() }
}
