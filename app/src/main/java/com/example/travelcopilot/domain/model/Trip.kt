package com.example.travelcopilot.domain.model
data class Trip(

    val id: Long = 0,

    val name: String,

    val destinationName: String? = null,

    val state: TripState = TripState.CREATED,

    val createdAt: Long,

    val startedAt: Long? = null,

    val completedAt: Long? = null,

    val totalDistanceMeters: Double = 0.0,

    val driveTimeMs: Long = 0,

    val idleTimeMs: Long = 0
) {

    val isActive: Boolean
        get() = state != TripState.COMPLETED
}