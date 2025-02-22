package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModelImpl @Inject constructor() : ViewModel(), HomeViewModel {

    private val snackbarState = mutableStateOf<HomeSnackbarState>(HomeSnackbarState.Idle)

    private val homeState = mutableStateOf<HomeState>(HomeState.HomeLoadedState)

    override fun getState() = homeState.value

    override fun getSnackbarState() = snackbarState.value

    override fun resetSnackbarState() {
        snackbarState.value = HomeSnackbarState.Idle
    }

    sealed class HomeState {
        data object HomeLoadedState: HomeState()
    }

    sealed class HomeSnackbarState {
        data object Idle : HomeSnackbarState()

        data class ShowGenericError(
            val error: String? = null,
        ) : HomeSnackbarState()
    }

    sealed class HousesNavRouteUi {
        data object Idle : HousesNavRouteUi()

//        data class GoToPlayerListUi(val houseName: String) : HousesNavRouteUi() {
//            fun getNavRouteWithArguments(): String = NavItem.PlayersList.getNavRouteWithArguments(houseName)
//        }
    }
}
