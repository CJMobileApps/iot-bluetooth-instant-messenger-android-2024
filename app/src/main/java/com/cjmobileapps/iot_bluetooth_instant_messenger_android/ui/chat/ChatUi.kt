package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel.HomeViewModel
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui.home.viewemodel.HomeViewModelImpl

@Composable
fun ChatUi(
    modifier: Modifier,
    homeViewModel: HomeViewModel,
    homeLoadedState: HomeViewModelImpl.HomeState.HomeLoadedState,
    navController: NavController,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 32.dp, end = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Chat Ui Screen")
    }
}
