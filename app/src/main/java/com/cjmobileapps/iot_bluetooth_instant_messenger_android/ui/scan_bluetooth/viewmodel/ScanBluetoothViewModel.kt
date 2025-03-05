package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.viewmodel

import android.bluetooth.BluetoothAdapter

interface ScanBluetoothViewModel {
    fun getState(): ScanBluetoothViewModelImpl.ScanBluetoothState

    fun getSnackbarState(): ScanBluetoothViewModelImpl.ScanBluetoothSnackbarState

    fun showBluetoothErrorSnackbar(errorMessage : String?)

    fun resetSnackbarState()

    fun getScanBluetoothNavRouteUiState(): ScanBluetoothViewModelImpl.ScanBluetoothNavRouteUi

    fun resetNavRouteUiToIdle()

    fun goToGoToChatUi()

    fun checkBluetoothPermissions()

    fun resetCheckBluetoothPermissions()

    fun enableBluetooth(bluetoothAdapter: BluetoothAdapter)

    fun isBluetoothEnabled(): Boolean

    fun getBluetoothAdapter(): BluetoothAdapter?
}
