package com.example.ladybugpermission.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ladybugpermission.ui.theme.LadybugPermissionTheme

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MyScreen.SPLASH.name,
        modifier = modifier
    ) {
        composable(MyScreen.SPLASH.name) {
            SplashScreen(
                onFinish = {
                    navController.navigate(MyScreen.HOME.name) {
                        popUpTo(MyScreen.SPLASH.name) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(MyScreen.HOME.name) {
            HomeScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LadybugPermissionTheme {
        MainScreen()
    }
}
