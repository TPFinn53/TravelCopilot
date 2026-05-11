package com.example.travelcopilot.core.context

import com.example.travelcopilot.utils.location.LocationData

data class LocationContext(
    val location: LocationData?,
    val placeName: String?,
    val city: String?,
    val regionState: String?,
    val distanceToDestinationMeters: Float?,
    val isStopped: Boolean
)
