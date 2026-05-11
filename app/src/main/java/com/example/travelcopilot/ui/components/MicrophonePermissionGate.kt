package com.example.travelcopilot.ui.components

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.travelcopilot.core.voice.PermissionState

@Composable
fun MicrophonePermissionGate(
    onPermissionGranted: @Composable () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var permissionState by remember { mutableStateOf(PermissionState.UNKNOWN) }

    fun checkPermission() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        permissionState = if (granted) {
            PermissionState.GRANTED
        } else {
            val shouldShowRationale =
                activity?.let {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        it,
                        Manifest.permission.RECORD_AUDIO
                    )
                } ?: false

            if (!shouldShowRationale) {
                PermissionState.PERMANENTLY_DENIED
            } else {
                PermissionState.DENIED
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        checkPermission()
    }

    LaunchedEffect(Unit) {
        checkPermission()
        if (permissionState == PermissionState.UNKNOWN ||
            permissionState == PermissionState.DENIED
        ) {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    when (permissionState) {

        PermissionState.GRANTED -> {
            onPermissionGranted()
        }

        PermissionState.DENIED -> {
            PermissionDeniedUI(
                onRetry = {
                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                }
            )
        }

        PermissionState.PERMANENTLY_DENIED -> {
            PermissionSettingsUI()
        }

        PermissionState.UNKNOWN -> {
            Text("Requesting microphone permission...")
        }
    }
}
@Composable
fun PermissionDeniedUI(onRetry: () -> Unit) {
    Column {
        Text("Microphone access is required for voice features.")

        Button(onClick = onRetry) {
            Text("Grant Permission")
        }
    }
}
@Composable
fun PermissionSettingsUI() {

    val context = LocalContext.current

    Column {
        Text("Microphone permission permanently denied.")

        Button(
            onClick = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.fromParts("package", context.packageName, null)
                )
                context.startActivity(intent)
            }
        ) {
            Text("Open Settings")
        }
    }
}