package com.example.travelcopilot.ui.components

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*

@Composable
fun RequestMicPermission(onGranted: () -> Unit) {

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) onGranted()
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.RECORD_AUDIO)
    }
}