package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.NavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor() : ViewModel(), HomeViewModel {

    private val snackbarState = mutableStateOf<HomeSnackbarState>(HomeSnackbarState.Idle)

    private val homeState = mutableStateOf<HomeState>(HomeState.HomeLoadedState())

    override fun getState() = homeState.value

    override fun getSnackbarState() = snackbarState.value

    override fun showBluetoothErrorSnackbar(errorMessage: String?) {
        snackbarState.value = HomeSnackbarState.ShowGenericError(errorMessage)
    }

    override fun resetSnackbarState() {
        snackbarState.value = HomeSnackbarState.Idle
    }

    override fun getHomeNavRouteUiState(): HomeNavRouteUi {
        val state = getState()
        if (state !is HomeState.HomeLoadedState) return HomeNavRouteUi.Idle
        return state.homeNavRouteUi.value
    }

    override fun resetNavRouteUiToIdle() {
        val state = getState()
        if (state !is HomeState.HomeLoadedState) return
        state.homeNavRouteUi.value = HomeNavRouteUi.Idle
    }

    override fun goToScanBluetoothUi() {
        val state = getState()
        if (state !is HomeState.HomeLoadedState) return
        state.homeNavRouteUi.value = HomeNavRouteUi.GoToScanBluetoothUi
    }

    override fun checkBluetoothPermissions() {
        val state = getState()
        if (state !is HomeState.HomeLoadedState) return
        state.checkBluetoothPermissions.value = true
    }

    override fun resetCheckBluetoothPermissions() {
        val state = getState()
        if (state !is HomeState.HomeLoadedState) return
        state.checkBluetoothPermissions.value = false

    }

    sealed class HomeState {
        data class HomeLoadedState(
            val checkBluetoothPermissions: MutableState<Boolean> = mutableStateOf(false),
            val homeNavRouteUi: MutableState<HomeNavRouteUi> = mutableStateOf(HomeNavRouteUi.Idle),
        ): HomeState()
    }

    sealed class HomeSnackbarState {
        data object Idle : HomeSnackbarState()

        data class ShowGenericError(
            val error: String? = null,
        ) : HomeSnackbarState()
    }

    sealed class HomeNavRouteUi {
        data object Idle : HomeNavRouteUi()

        data object GoToScanBluetoothUi : HomeNavRouteUi() {
            fun getNavRoute(): String = NavItem.ScanBluetooth.getNavRouteWithArguments()
        }
    }
}
