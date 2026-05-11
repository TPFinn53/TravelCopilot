package com.example.travelcopilot.ui.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {

    val items = listOf(
        Screen.Main,
        Screen.Trip,
        Screen.Chat,
        Screen.Settings
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {

        items.forEach { screen ->

            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {

                        // 🔥 THIS is the important part
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }

                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(screen.label) },
                icon = {
                    Text(
                        when (screen) {
                            Screen.Main -> "🏠"
                            Screen.Trip -> "🚗"
                            Screen.Chat -> "💬"
                            Screen.Settings -> "⚙"
                        }
                    )
                }
            )
        }
    }
}