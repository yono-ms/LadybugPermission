package com.example.ladybugpermission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
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
import androidx.lifecycle.lifecycleScope
import com.example.ladybugpermission.database.LocationEntity
import com.example.ladybugpermission.database.MyDatabase
import com.example.ladybugpermission.ui.screen.MainScreen
import com.example.ladybugpermission.ui.theme.LadybugPermissionTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.Date

class MainActivity : ComponentActivity() {
    private val isShowRationale = MutableStateFlow(false)
    private val isShowDenied = MutableStateFlow(false)

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            logger.trace("LocationCallback onLocationResult {}", locationResult)
            lifecycleScope.launch {
                runCatching {
                    val dao = MyDatabase.getDatabase(this@MainActivity).locationDao()
                    for (location in locationResult.locations) {
                        dao.insertLocation(
                            LocationEntity(
                                locationId = 0,
                                latitude = location.latitude,
                                longitude = location.longitude,
                                updateAt = Date().time
                            )
                        )
                    }
                }.onFailure {
                    logger.error("LocationDao insert", it)
                }
            }
        }
    }

    private fun startGetLocation() {
        logger.trace("startGetLocation START")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                10_000
            ).build()
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            isRequesting.value = true
        }
        logger.trace("startGetLocation END")
    }

    private fun stopGetLocation() {
        logger.trace("stopGetLocation START")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        logger.trace("stopGetLocation END")
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        logger.info("RequestMultiplePermissions result {}", permissions)
        // WorkFlow 7 Does the user grant permission to your app?
        permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false).also {
            isGrantedFineLocation.value = it
            isShowDenied.value = !it
            startGetLocation()
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

    private fun startUpdateLocation() {
        logger.trace("startUpdateLocation START")
        // WorkFlow 4 Permission already granted to your app?
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            logger.debug("ACCESS_FINE_LOCATION PERMISSION_GRANTED")
            // WorkFlow 8a Access the Info that's protected by the permission
            startGetLocation()
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

    private fun stopUpdateLocation() {
        logger.trace("stopUpdateLocation START")
        stopGetLocation()
        isRequesting.value = false
        logger.trace("stopUpdateLocation END")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loggerTest()
        logger.info("onCreate savedInstanceState=$savedInstanceState")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        enableEdgeToEdge()
        setContent {
            LadybugPermissionTheme {
                val rationale = isShowRationale.collectAsState()
                val denied = isShowDenied.collectAsState()
                Box {
                    MainScreen(
                        start = {
                            startUpdateLocation()
                        }, stop = {
                            stopUpdateLocation()
                        }
                    )
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
        DENIED,
        REQUESTING,
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        logger.trace("onRestoreInstanceState {}", savedInstanceState)
        isShowRationale.value = savedInstanceState.getBoolean(BundleKey.RATIONALE.name, false)
        isShowDenied.value = savedInstanceState.getBoolean(BundleKey.DENIED.name, false)
        isRequesting.value = savedInstanceState.getBoolean(BundleKey.REQUESTING.name, false)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        logger.trace("onSaveInstanceState {}", outState)
        outState.putBoolean(BundleKey.RATIONALE.name, isShowRationale.value)
        outState.putBoolean(BundleKey.DENIED.name, isShowDenied.value)
        outState.putBoolean(BundleKey.REQUESTING.name, isRequesting.value)
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
        if (isGrantedFineLocation.value) {
            if (isRequesting.value) {
                logger.trace("restart Update location")
                startUpdateLocation()
            }
        } else {
            if (isRequesting.value) {
                logger.trace("teardown Update location")
                isRequesting.value = false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        logger.trace("onPause")
        stopGetLocation()
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
val isRequesting = MutableStateFlow(false)

val logger: Logger by lazy { LoggerFactory.getLogger("LadybugP") }
