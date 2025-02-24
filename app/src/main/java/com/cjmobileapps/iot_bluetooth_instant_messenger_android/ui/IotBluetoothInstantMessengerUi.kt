package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.theme.IotBluetoothInstantMessengerAndroid2024Theme

@Composable
fun IotBluetoothInstantMessengerUi() {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    IotBluetoothInstantMessengerAndroid2024Theme {
        Scaffold { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavigationGraph(
                    navController = navController,
                    coroutineScope = coroutineScope,
                    snackbarHostState = snackbarHostState,
                )
            }
        }
    }
}
