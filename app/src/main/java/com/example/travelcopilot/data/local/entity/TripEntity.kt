package com.example.travelcopilot.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

import com.example.travelcopilot.domain.model.TripState

@Entity(
    tableName = "trips",

    indices = [
        Index("state"),
        Index("createdAt"),
        Index("startedAt")
    ]
)
data class TripEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // =========================================================
    // Basic trip info
    // =========================================================

    val name: String,

    val destinationName: String? = null,

    // =========================================================
    // Trip lifecycle
    // =========================================================

    val state: TripState = TripState.CREATED,

    val createdAt: Long = System.currentTimeMillis(),

    val startedAt: Long? = null,

    val stoppedAt: Long? = null,

    val completedAt: Long? = null,

    // =========================================================
    // Live location snapshot
    // =========================================================

    val currentLat: Double? = null,

    val currentLng: Double? = null,

    val currentLocationName: String? = null,

    // =========================================================
    // Trip metrics
    // =========================================================

    val totalDistanceMeters: Float = 0f,

    val driveTimeMs: Long = 0L,

    val idleTimeMs: Long = 0L,

    // =========================================================
    // Optional notes / metadata
    // =========================================================

    val notes: String? = null
)