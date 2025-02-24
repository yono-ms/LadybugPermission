package com.example.ladybugpermission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.core.app.ActivityCompat
import com.example.ladybugpermission.ui.screen.MainScreen
import com.example.ladybugpermission.ui.theme.LadybugPermissionTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MainActivity : ComponentActivity() {
    private val isShowRationale = MutableStateFlow(false)
    private val isShowDenied = MutableStateFlow(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        logger.info("RequestMultiplePermissions result {}", permissions)
        // WorkFlow 7 Does the user grant permission to your app?
        permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false).also {
            isGrantedFineLocation.value = it
            if (it) {
                logger.debug("TODO GET LOCATION")
            } else {
                isShowDenied.value = true
            }
        }
    }

    private fun getLocationPermission() {
        logger.trace("getLocationPermission START")
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        logger.trace("getLocationPermission END")
    }

    fun startUpdateLocation() {
        logger.trace("startUpdateLocation START")
        // WorkFlow 4 Permission already granted to your app?
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            logger.debug("ACCESS_FINE_LOCATION PERMISSION_GRANTED")
            // WorkFlow 8a Access the Info that's protected by the permission
            logger.debug("TODO Get Location")
        } else {
            logger.debug("ACCESS_FINE_LOCATION PERMISSION_DENIED")
            // WorkFlow 5a Show a rationale to the user?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                logger.debug("shouldShowRequestPermissionRationale true. Show dialog.")
                // WorkFlow 5b Explain to the user why your app needs this permission
                isShowRationale.value = true
            } else {
                logger.debug("shouldShowRequestPermissionRationale false. Get permission.")
                // WorkFlow 6 Request the permission to show the system dialog
                getLocationPermission()
            }
        }
        logger.trace("startUpdateLocation END")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loggerTest()
        logger.info("onCreate savedInstanceState=$savedInstanceState")
        enableEdgeToEdge()
        setContent {
            LadybugPermissionTheme {
                val rationale = isShowRationale.collectAsState()
                val denied = isShowDenied.collectAsState()
                Box {
                    MainScreen()
                    if (rationale.value) {
                        AlertDialog(
                            onDismissRequest = {},
                            confirmButton = {
                                TextButton(onClick = {
                                    logger.trace("rationale dialog onClick OK")
                                    // WorkFlow 6 Request the permission to show the system dialog
                                    getLocationPermission()
                                    isShowRationale.value = false
                                }) {
                                    Text(text = "OK")
                                }
                            },
                            title = { Text(text = "WorkFlow 5b") },
                            text = { Text(text = "Application needs permission for location update.") }
                        )
                    }
                    if (denied.value) {
                        AlertDialog(
                            onDismissRequest = {},
                            confirmButton = {
                                TextButton(onClick = {
                                    isShowDenied.value = false
                                }) {
                                    Text(text = "OK")
                                }
                            },
                            title = { Text(text = "Information") },
                            text = { Text(text = "Application needs ACCESS FINE LOCATION") }
                        )
                    }
                }
            }
        }
    }

    enum class BundleKey {
        RATIONALE,
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        logger.trace("onRestoreInstanceState {}", savedInstanceState)
        isShowRationale.value = savedInstanceState.getBoolean(BundleKey.RATIONALE.name, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        logger.trace("onSaveInstanceState {}", outState)
        outState.putBoolean(BundleKey.RATIONALE.name, isShowRationale.value)
    }

    override fun onStart() {
        super.onStart()
        logger.trace("onStart")
    }

    override fun onResume() {
        super.onResume()
        logger.trace("onResume")
        isGrantedFineLocation.value = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onPause() {
        super.onPause()
        logger.trace("onPause")
    }

    override fun onStop() {
        super.onStop()
        logger.trace("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.trace("onDestroy")
    }

    private fun loggerTest() {
        logger.trace("Logger TEST")
        logger.debug("Logger TEST")
        logger.info("Logger TEST")
        logger.warn("Logger TEST")
        logger.error("Logger TEST")
    }
}

val isGrantedFineLocation = MutableStateFlow(false)

val logger: Logger by lazy { LoggerFactory.getLogger("LadybugP") }
