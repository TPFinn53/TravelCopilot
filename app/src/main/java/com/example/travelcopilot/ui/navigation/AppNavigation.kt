package com.example.travelcopilot.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.travelcopilot.ui.screens.MainScreen
import com.example.travelcopilot.ui.screens.TripScreen
import com.example.travelcopilot.ui.screens.ChatScreen
import com.example.travelcopilot.ui.screens.settings.SettingsScreen
import com.example.travelcopilot.ui.screens.settings.StorageSettingsScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Screen.Main.route,
            modifier = Modifier.padding(padding) // ✅ FIX
        ) {
            composable("storage_settings") {
                StorageSettingsScreen()
            }
            composable("settings") {
                SettingsScreen()
            }
            composable(Screen.Main.route) {
                MainScreen()
            }
            composable(Screen.Trip.route) {
                TripScreen(onOpenChat = { navController.popBackStack() })
            }
            composable(Screen.Chat.route) {
                ChatScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}