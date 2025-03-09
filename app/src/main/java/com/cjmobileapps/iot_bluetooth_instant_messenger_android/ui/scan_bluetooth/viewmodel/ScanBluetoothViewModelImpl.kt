package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.viewmodel

import android.bluetooth.BluetoothAdapter
import android.icu.util.TimeUnit
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.NavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanBluetoothViewModelImpl @Inject constructor() : ViewModel(), ScanBluetoothViewModel {

    private val snackbarState =
        mutableStateOf<ScanBluetoothSnackbarState>(ScanBluetoothSnackbarState.Idle)

    private val scanBluetoothState =
        mutableStateOf<ScanBluetoothState>(ScanBluetoothState.ScanBluetoothLoadedState())

    override fun getState() = scanBluetoothState.value

    override fun getSnackbarState() = snackbarState.value

    override fun showBluetoothErrorSnackbar(errorMessage: String?) {
        snackbarState.value = ScanBluetoothSnackbarState.ShowGenericError(errorMessage)
    }

    override fun resetSnackbarState() {
        snackbarState.value = ScanBluetoothSnackbarState.Idle
    }

    override fun getScanBluetoothNavRouteUiState(): ScanBluetoothNavRouteUi {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return ScanBluetoothNavRouteUi.Idle
        return state.scanBluetoothNavRouteUi.value
    }

    override fun resetNavRouteUiToIdle() {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return
        state.scanBluetoothNavRouteUi.value = ScanBluetoothNavRouteUi.Idle
    }

    override fun goToGoToChatUi() {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return
        state.scanBluetoothNavRouteUi.value = ScanBluetoothNavRouteUi.GoToChatUi
    }

    override fun checkBluetoothPermissions() {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return
        state.checkBluetoothPermissions.value = true
    }

    override fun resetCheckBluetoothPermissions() {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return
        state.checkBluetoothPermissions.value = false

    }

    override fun enableBluetooth(bluetoothAdapter: BluetoothAdapter) {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return
        state.bluetoothAdapter.value = bluetoothAdapter
        scanFor10Seconds(state)
    }

    override fun isBluetoothEnabled(): Boolean {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return false
        val bluetoothAdapter = state.bluetoothAdapter.value
        return state.isBlueToothEnabled.value && bluetoothAdapter != null && bluetoothAdapter.isEnabled
    }

    override fun getBluetoothAdapter(): BluetoothAdapter? {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return null
        return state.bluetoothAdapter.value
    }

    private fun scanFor10Seconds(state: ScanBluetoothState.ScanBluetoothLoadedState) {
        state.isScanning.value = true
        GlobalScope.launch {
            delay(java.util.concurrent.TimeUnit.SECONDS.toMillis(5))
            println("Coroutine Done!")
            state.isScanning.value = false
        }
    }

    sealed class ScanBluetoothState {
        data class ScanBluetoothLoadedState(
            val checkBluetoothPermissions: MutableState<Boolean> = mutableStateOf(true),
            val isBlueToothEnabled: MutableState<Boolean> = mutableStateOf(true), //TODO state methods for this
            val scanBluetoothNavRouteUi: MutableState<ScanBluetoothNavRouteUi> = mutableStateOf(
                ScanBluetoothNavRouteUi.Idle
            ),
            val bluetoothAdapter: MutableState<BluetoothAdapter?> = mutableStateOf(null),
            val isScanning: MutableState<Boolean> = mutableStateOf(false)
        ) : ScanBluetoothState()
    }

    sealed class ScanBluetoothSnackbarState {
        data object Idle : ScanBluetoothSnackbarState()

        data class ShowGenericError(
            val error: String? = null,
        ) : ScanBluetoothSnackbarState()
    }

    sealed class ScanBluetoothNavRouteUi {
        data object Idle : ScanBluetoothNavRouteUi()

        data object GoToChatUi : ScanBluetoothNavRouteUi() {
            fun getNavRoute(): String = NavItem.ScanBluetooth.getNavRouteWithArguments()
        }
    }
}
