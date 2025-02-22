package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel

interface HomeViewModel {
    fun getState(): HomeViewModelImpl.HomeState

    fun getSnackbarState(): HomeViewModelImpl.HomeSnackbarState
//
//    fun getHousesNavRouteUiState(): HousesViewModelImpl.HousesNavRouteUi

    fun resetSnackbarState()

//    fun resetNavRouteUiToIdle()

//    fun goToPlayersListUi(houseName: String)
}