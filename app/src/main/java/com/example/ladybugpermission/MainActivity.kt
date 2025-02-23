package com.example.ladybugpermission

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ladybugpermission.ui.screen.MainScreen
import com.example.ladybugpermission.ui.theme.LadybugPermissionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LadybugPermissionTheme {
                MainScreen()
            }
        }
    }
}
