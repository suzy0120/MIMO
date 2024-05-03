package com.mimo.android

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.journeyapps.barcodescanner.ScanContract
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.mimo.android.health.HealthConnectManager
import com.mimo.android.health.checkAvailability
import com.mimo.android.health.checkHealthConnectPermission
import com.mimo.android.health.createHealthConnectPermissionRequest
import com.mimo.android.qr.checkCameraPermission
import com.mimo.android.qr.createQRRequestPermissionLauncher
import com.mimo.android.service.ExampleLocationForegroundService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val TAG = "Mimo"

class MainActivity : ComponentActivity() {
    private var qrData by mutableStateOf<String?>(null)

    // QR code Scanner
    private val barCodeLauncher = registerForActivityResult(ScanContract()) {
            result ->
        if (result.contents == null) {
            qrData = null
            Toast.makeText(
                this@MainActivity,
                "취소",
                Toast.LENGTH_SHORT
            ).show()
            return@registerForActivityResult
        }
        qrData = result.contents
        Toast.makeText(
            this@MainActivity,
            "${result.contents}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private val qRRequestPermissionLauncher = createQRRequestPermissionLauncher(
        barCodeLauncher = barCodeLauncher
    )

    // health-connect
    private lateinit var healthConnectManager: HealthConnectManager
    private lateinit var healthConnectPermissionRequest: ActivityResultLauncher<Set<String>>

    // location-foreground sample
    private var exampleService: ExampleLocationForegroundService? = null
    private var serviceBoundState by mutableStateOf(false)
    private var displayableLocation by mutableStateOf<String?>(null)

    // needed to communicate with the service.
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // we've bound to ExampleLocationForegroundService, cast the IBinder and get ExampleLocationForegroundService instance.
            Log.d(TAG, "onServiceConnected")

            val binder = service as ExampleLocationForegroundService.LocalBinder
            exampleService = binder.getService()
            serviceBoundState = true

            onServiceConnected()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            // This is called when the connection with the service has been disconnected. Clean up.
            Log.d(TAG, "onServiceDisconnected")

            serviceBoundState = false
            exampleService = null
        }
    }

    // we need notification permission to be able to display a notification for the foreground service
    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // if permission was denied, the service can still run only the notification won't be visible
        }

    // we need location permission to be able to start the service
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted, service can run
                startForegroundService()
            }

            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted, service can still run.
                startForegroundService()
            }

            else -> {
                // No location access granted, service can't be started as it will crash
                Toast.makeText(this, "Location permission is required!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var keyHash = Utility.getKeyHash(this)
        Log.e(TAG, "해시 키 값 : ${keyHash}")

        // kakao sdk 초기화
        KakaoSdk.init(this, "2fa7b989f12635ed266010d85b0a513e")

        healthConnectManager = (application as BaseApplication).healthConnectManager

        // check health-connect permission
        if (checkAvailability()) {
            healthConnectPermissionRequest = createHealthConnectPermissionRequest(
                healthConnectManager = healthConnectManager,
                context = this
            )
            checkHealthConnectPermission(
                showInfo = false,
                healthConnectManager = healthConnectManager,
                context = this,
                healthConnectPermissionRequest = healthConnectPermissionRequest
            )
        }

        // check location permission
        checkAndRequestNotificationPermission()
        tryToBindToServiceIfRunning()

        setContent {
            MimoApp(
                healthConnectManager = healthConnectManager,
                context = this,
                serviceRunning = serviceBoundState,
                currentLocation = displayableLocation,
                onClickForeground = ::onStartOrStopForegroundServiceClick,
                checkCameraPermission = { checkCameraPermission(
                    context = this,
                    barCodeLauncher = barCodeLauncher,
                    qRRequestPermissionLauncher = qRRequestPermissionLauncher
                ) },
                qrData = qrData
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }

    /**
     * Check for notification permission before starting the service so that the notification is visible
     */
    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    // permission already granted
                }

                else -> {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun onStartOrStopForegroundServiceClick() {
        if (exampleService == null) {
            // service is not yet running, start it after permission check
            locationPermissionRequest.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            // service is already running, stop it
            exampleService?.stopForegroundService()
        }
    }

    /**
     * Creates and starts the ExampleLocationForegroundService as a foreground service.
     *
     * It also tries to bind to the service to update the UI with location updates.
     */
    private fun startForegroundService() {
        // start the service
        startForegroundService(Intent(this, ExampleLocationForegroundService::class.java))

        // bind to the service to update UI
        tryToBindToServiceIfRunning()
    }

    private fun tryToBindToServiceIfRunning() {
        Intent(this, ExampleLocationForegroundService::class.java).also { intent ->
            bindService(intent, connection, 0)
        }
    }

    private fun onServiceConnected() {
        lifecycleScope.launch {
            // observe location updates from the service
            exampleService?.locationFlow?.map {
                it?.let { location ->
                    "${location.latitude}, ${location.longitude}"
                }
            }?.collectLatest {
                displayableLocation = it
            }
        }
    }
}