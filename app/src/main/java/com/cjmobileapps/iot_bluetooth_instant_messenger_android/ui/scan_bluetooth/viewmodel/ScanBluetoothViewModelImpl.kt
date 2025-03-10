package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.scan_bluetooth.viewmodel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.NavItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ScanBluetoothViewModelImpl @Inject constructor() : ViewModel(), ScanBluetoothViewModel {
    private val tag = ScanBluetoothViewModelImpl::class.java.simpleName

    private val compositeJob = Job()

    private val foundDevicesFlow = MutableSharedFlow<BluetoothDevice>()

    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Timber.tag(tag)
                .e("coroutineExceptionHandler() error occurred: $throwable \n ${throwable.message}")
            snackbarState.value = ScanBluetoothSnackbarState.ShowGenericError()
        }

    private val coroutineContext =
        compositeJob + Dispatchers.Main + exceptionHandler + SupervisorJob()

    private val snackbarState =
        mutableStateOf<ScanBluetoothSnackbarState>(ScanBluetoothSnackbarState.Idle)

    private val scanBluetoothState =
        mutableStateOf<ScanBluetoothState>(ScanBluetoothState.ScanBluetoothLoadedState())

    init {
        viewModelScope.launch(coroutineContext + Dispatchers.Default) {
            foundDevicesFlow
                .collect { device ->
                    delay(50) //Slow down bluetooth device found firing off and a keep recomposing the screen over and over again
                    addDevice(device)
                }
        }
    }

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
        viewModelScope.launch(Dispatchers.Main) {
            foundDevicesFlow.emit(device)
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun addDevice(device: BluetoothDevice) {
        withContext(Dispatchers.Main) {
            val state = getState()
            if (state !is ScanBluetoothState.ScanBluetoothLoadedState) return@withContext
            val foundDevices = state.foundDevicesList

            if (!foundDevices.contains(device)) {
                foundDevices.add(device)
                Timber.tag(tag).d("BLE device added: ${device.name} : ${device.address} ")
            }
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
        viewModelScope.launch(coroutineContext) {
            withContext(Dispatchers.IO) {
                delay(java.util.concurrent.TimeUnit.SECONDS.toMillis(5))
                state.isScanning.value = false
            }
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
