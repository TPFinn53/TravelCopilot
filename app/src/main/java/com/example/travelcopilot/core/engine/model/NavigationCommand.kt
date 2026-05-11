package com.example.travelcopilot.core.engine.model

sealed class NavigationCommand {
    data class NavigateTo(val destination: String) : NavigationCommand()
    data class NavigateToCoordinates(val lat: Double, val lng: Double) : NavigationCommand()
    object CancelNavigation : NavigationCommand()
}