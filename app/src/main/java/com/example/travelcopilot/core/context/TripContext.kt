package com.example.travelcopilot.core.context

import com.example.travelcopilot.domain.model.TripState

data class TripContext(
    val tripId: Long,
    val tripName: String,
    val tripState: TripState?
)