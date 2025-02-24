package com.example.ladybugpermission.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ladybugpermission.logger
import com.example.ladybugpermission.ui.theme.LadybugPermissionTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinish: () -> Unit) {
    LaunchedEffect(Unit) {
        logger.trace("SplashScreen LaunchedEffect START")
        delay(1_000)
        onFinish()
        logger.trace("SplashScreen LaunchedEffect END")
    }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("loading")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    LadybugPermissionTheme {
        SplashScreen({})
    }
}
