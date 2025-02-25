package com.example.ladybugpermission.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.ladybugpermission.MainActivity
import com.example.ladybugpermission.isGrantedFineLocation
import com.example.ladybugpermission.isRequesting
import com.example.ladybugpermission.logger
import com.example.ladybugpermission.ui.theme.LadybugPermissionTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val granted = isGrantedFineLocation.collectAsState()
    val requesting = isRequesting.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = MyScreen.HOME.title) },
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("ACCESS_FINE_LOCATION : ${granted.value}")
            val activity = context as MainActivity
            Button(
                onClick = {
                    logger.trace("onClick START update location")
                    activity.startUpdateLocation()
                },
                enabled = !requesting.value
            ) {
                Text("START update location")
            }
            Button(
                onClick = {
                    logger.trace("onClick STOP update location")
                    activity.stopUpdateLocation()
                },
                enabled = requesting.value
            ) {
                Text("STOP update location")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LadybugPermissionTheme {
        HomeScreen()
    }
}
