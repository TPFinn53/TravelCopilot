package com.example.travelcopilot.utils.location

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val speed: Float,

    val placeName: String?,   // ✅ ADD THIS
    val city: String?,        // ✅ ADD THIS
    val regionState: String?  // ✅ ADD THIS
)