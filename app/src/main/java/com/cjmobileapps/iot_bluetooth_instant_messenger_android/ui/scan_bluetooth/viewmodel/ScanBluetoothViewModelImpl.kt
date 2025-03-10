package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.NavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ScanBluetoothViewModelImpl @Inject constructor() : ViewModel(), ScanBluetoothViewModel {
    private val tag = ScanBluetoothViewModelImpl::class.java.simpleName

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

    override fun setShouldCheckForBluetoothPermissions() {
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

    @SuppressLint("MissingPermission")
    override fun foundDevice(device: BluetoothDevice) {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return
        val foundDevices = state.foundDevicesList
        if (!foundDevices.contains(device) && device.name != null) {
            foundDevices.add(device)
            Timber.tag(tag).d("BLE Found device: ${device.name}")
        }
    }

    override fun clearAllFoundDevices() {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return
        state.foundDevicesList.clear()
    }

    override fun isScanning(): Boolean {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return false
        return state.isScanning.value
    }


    override fun checkBluetoothPermissions(): Boolean {
        val state = getState()
        if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return false
        return state.checkBluetoothPermissions.value
    }

    private fun scanFor10Seconds(state: ScanBluetoothState.ScanBluetoothLoadedState) {
        state.isScanning.value = true
        //TODO fix this
        GlobalScope.launch {
            delay(java.util.concurrent.TimeUnit.SECONDS.toMillis(5))
            println("Coroutine Done!")
            state.isScanning.value = false
            //TODO after done scanning show list instead of just keep updating
        }
    }

    sealed class ScanBluetoothState {
        data class ScanBluetoothLoadedState(
            val checkBluetoothPermissions: MutableState<Boolean> = mutableStateOf(true),
            val isBlueToothEnabled: MutableState<Boolean> = mutableStateOf(true),
            val scanBluetoothNavRouteUi: MutableState<ScanBluetoothNavRouteUi> = mutableStateOf(
                ScanBluetoothNavRouteUi.Idle
            ),
            val bluetoothAdapter: MutableState<BluetoothAdapter?> = mutableStateOf(null),
            val isScanning: MutableState<Boolean> = mutableStateOf(false),
            val foundDevicesList: SnapshotStateList<BluetoothDevice> = mutableStateListOf(),
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
