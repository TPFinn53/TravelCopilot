package com.example.travelcopilot.data.local.mapper

import com.example.travelcopilot.data.local.entity.TripEntity
import com.example.travelcopilot.domain.model.Trip
import com.example.travelcopilot.domain.model.TripState

// =============================================================
// Entity -> Domain
// =============================================================

fun TripEntity.toTrip(): Trip {

    return Trip(

        id = id,
        name = name,
        destinationName = destinationName,
        state = state,
        createdAt = createdAt,
        startedAt = startedAt,
        completedAt = completedAt,
        totalDistanceMeters = totalDistanceMeters.toDouble(),
        driveTimeMs = driveTimeMs,
        idleTimeMs = idleTimeMs,
    )
}

// =============================================================
// Domain -> Entity
// =============================================================

fun Trip.toEntity(): TripEntity {

    return TripEntity(

        id = id,

        // =====================================================
        // Basic trip info
        // =====================================================

        name = name,
        destinationName = destinationName,

        // =====================================================
        // Lifecycle
        // =====================================================

        state = state,
        createdAt = createdAt,
        startedAt = startedAt,
        completedAt = completedAt,
        stoppedAt =
            if (state ==
                TripState.STOPPED
            ) {
                completedAt
            } else {
                null
            },

        // =====================================================
        // Metrics
        // =====================================================

        totalDistanceMeters = totalDistanceMeters.toFloat(),
        driveTimeMs = driveTimeMs,
        idleTimeMs = idleTimeMs
    )
}