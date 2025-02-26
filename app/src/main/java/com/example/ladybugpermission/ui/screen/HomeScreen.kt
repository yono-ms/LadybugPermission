package com.example.ladybugpermission.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.ladybugpermission.database.LocationEntity
import com.example.ladybugpermission.database.MyDatabase
import com.example.ladybugpermission.extension.toBestString
import com.example.ladybugpermission.isGrantedFineLocation
import com.example.ladybugpermission.isRequesting
import com.example.ladybugpermission.logger
import com.example.ladybugpermission.ui.theme.LadybugPermissionTheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(start: () -> Unit, stop: () -> Unit) {
    val context = LocalContext.current
    val dao = MyDatabase.getDatabase(context).locationDao()
    val locations by dao.getAllLocationsFlow().collectAsState(initial = listOf())
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
            Button(
                onClick = {
                    logger.trace("onClick START update location")
                    start()
                },
                enabled = !requesting.value
            ) {
                Text("START update location")
            }
            Button(
                onClick = {
                    logger.trace("onClick STOP update location")
                    stop()
                },
                enabled = requesting.value
            ) {
                Text("STOP update location")
            }
            HorizontalDivider()
            LazyColumn {
                items(locations) {
                    LocationItem(locationEntity = it)
                    HorizontalDivider()
                }
            }
            Spacer(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun LocationItem(locationEntity: LocationEntity) {
    val latitude = locationEntity.latitude.toString()
    val longitude = locationEntity.longitude.toString()
    val updateAt = Date(locationEntity.updateAt).toBestString()
    Column {
        Row {
            Text(
                text = latitude,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1F)
            )
            Spacer(modifier = Modifier.weight(1F))
            Text(
                text = longitude,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1F)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "udateAt",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = updateAt,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    LadybugPermissionTheme {
        HomeScreen({}, {})
    }
}

@Preview(showBackground = true)
@Composable
fun LocationItemPreview() {
    LadybugPermissionTheme {
        LocationItem(
            LocationEntity(
                locationId = 0,
                latitude = 43.1234567,
                longitude = 135.123456,
                updateAt = 0
            )
        )
    }
}
