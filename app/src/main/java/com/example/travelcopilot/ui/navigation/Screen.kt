package com.example.travelcopilot.ui.navigation

object NavRoutes {
    const val TRIP_LIST = "trip_list"
    const val TRIP_DETAIL = "trip_detail"
}
sealed class Screen(
    val route: String,
    val label:String
) {

    data object Main : Screen("main","Home")

    data object Chat : Screen("chat/{tripId}","Chat") {

        fun createRoute(tripId: Long): String {
            return "chat/$tripId"
        }
    }

    data object Trip : Screen("trip/{tripId}","Trip") {

        fun createRoute(tripId: Long): String {
            return "trip/$tripId"
        }
    }

    data object Settings : Screen("settings","Settings")
}