package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel

interface HomeViewModel {
    fun getState(): HomeViewModelImpl.HomeState

    fun getSnackbarState(): HomeViewModelImpl.HomeSnackbarState

    fun showBluetoothErrorSnackbar(errorMessage : String?)

    fun resetSnackbarState()

    fun getHomeNavRouteUiState(): HomeViewModelImpl.HomeNavRouteUi

    fun resetNavRouteUiToIdle()

    fun goToScanBluetoothUi()

    fun checkBluetoothPermissions()

    fun resetCheckBluetoothPermissions()
}
