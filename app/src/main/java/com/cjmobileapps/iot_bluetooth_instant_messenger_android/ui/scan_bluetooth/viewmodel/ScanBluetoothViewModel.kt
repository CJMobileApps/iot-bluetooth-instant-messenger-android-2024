package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.viewmodel

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice

interface ScanBluetoothViewModel {
    fun getState(): ScanBluetoothViewModelImpl.ScanBluetoothState

    fun getSnackbarState(): ScanBluetoothViewModelImpl.ScanBluetoothSnackbarState

    fun showBluetoothErrorSnackbar(errorMessage : String?)

    fun resetSnackbarState()

    fun getScanBluetoothNavRouteUiState(): ScanBluetoothViewModelImpl.ScanBluetoothNavRouteUi

    fun resetNavRouteUiToIdle()

    fun goToGoToChatUi()

    fun setShouldCheckForBluetoothPermissions()

    fun resetCheckBluetoothPermissions()

    fun enableBluetooth(bluetoothAdapter: BluetoothAdapter)

    fun isBluetoothEnabled(): Boolean

    fun getBluetoothAdapter(): BluetoothAdapter?

    fun foundDevice(device: BluetoothDevice)

    fun clearAllFoundDevices()

    fun isScanning(): Boolean

    fun checkBluetoothPermissions(): Boolean
}
