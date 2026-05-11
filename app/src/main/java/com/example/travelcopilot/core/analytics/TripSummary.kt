package com.example.travelcopilot.core.analytics

data class TripSummary(
    val tripId: Long,
    val driveTimeMs: Long,
    val idleTimeMs: Long,
    val stopCount: Int,
    val noteCount: Int
)