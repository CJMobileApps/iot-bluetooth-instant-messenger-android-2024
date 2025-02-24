package com.cjmobileapps.iot_bluetooth_instant_messenger_android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.cjmobileapps.iot_bluetooth_instant_messenger_android.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IotBluetoothInstantMessengerTopAppBar(
    navController: NavController,
    topBarTitle: String = "",
) {
    val horizontalArrangement =
        if (navController.previousBackStackEntry != null) Arrangement.Start else Arrangement.Center

    val titleText =
        if (!topBarTitle.isNullOrEmpty()) {
            topBarTitle
        } else {
            stringResource(id = R.string.app_name)
        }

    TopAppBar(
        modifier =
        Modifier
            .fillMaxWidth(),
        title = {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .testTag("$titleText title"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = horizontalArrangement,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = titleText,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }
        },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_description),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
        colors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onSecondary,
        ),
    )
}
