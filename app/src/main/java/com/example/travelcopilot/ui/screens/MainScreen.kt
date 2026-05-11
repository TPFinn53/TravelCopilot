package com.example.travelcopilot.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travelcopilot.infra.copilot.CopilotService
import com.example.travelcopilot.ui.components.MicrophonePermissionGate
import com.example.travelcopilot.ui.components.VoiceIndicator
import com.example.travelcopilot.viewmodel.MainViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home

@Composable
fun MainScreen(
    viewModel: MainViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val context = LocalContext.current
    var serviceStarted by remember { mutableStateOf(false) }

    MicrophonePermissionGate {

        LaunchedEffect(Unit) {
            if (!serviceStarted) {
                val intent = Intent(context, CopilotService::class.java).apply {
                    action = CopilotService.ACTION_START
                }
                context.startService(intent)
                serviceStarted = true
            }
        }

        val voiceState by viewModel.voiceState.collectAsState()
        val amplitude by viewModel.amplitude.collectAsState()

        Column {

            VoiceIndicator(
                state = voiceState,
                amplitude = amplitude
            )
        }
    }
}