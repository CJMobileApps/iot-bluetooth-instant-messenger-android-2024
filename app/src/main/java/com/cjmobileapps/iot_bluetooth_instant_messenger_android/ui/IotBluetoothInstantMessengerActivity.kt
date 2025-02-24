package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IotBluetoothInstantMessengerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IotBluetoothInstantMessengerUi()
        }
    }
}
