package com.example.travelcopilot.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.travelcopilot.infra.copilot.CopilotService
import com.example.travelcopilot.ui.navigation.AppNavigation
import com.example.travelcopilot.ui.screens.MainScreen
import com.example.travelcopilot.ui.theme.TravelCopilotTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TravelCopilotTheme {
                AppNavigation()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // 🚀 Start Copilot Service
        val intent = Intent(this, CopilotService::class.java).apply {
            action = CopilotService.ACTION_START
        }
        startService(intent)
    }

    override fun onStop() {
        super.onStop()

        // 🛑 Stop Copilot Service (optional — depends on design)
        val intent = Intent(this, CopilotService::class.java).apply {
            action = CopilotService.ACTION_STOP
        }
        startService(intent)
    }
}