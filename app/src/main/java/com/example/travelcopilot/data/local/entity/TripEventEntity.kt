package com.example.travelcopilot.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

import com.example.travelcopilot.domain.model.TripEventType

@Entity(
    tableName = "trip_events",

    indices = [
        Index("tripId"),
        Index("timestamp"),
        Index("type")
    ]
)
data class TripEventEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // =========================================================
    // Relationship
    // =========================================================

    val tripId: Long,

    // =========================================================
    // Core event data
    // =========================================================

    val type: TripEventType,

    val timestamp: Long,

    // =========================================================
    // Optional location context
    // =========================================================

    val lat: Double? = null,

    val lng: Double? = null,

    val locationName: String? = null,

    // =========================================================
    // Optional content
    // =========================================================

    val message: String? = null,

    val uri: String? = null,

    // =========================================================
    // Optional metrics
    // =========================================================

    val durationMs: Long? = null,

    val speedMps: Float? = null,

    // =========================================================
    // Optional extensible metadata
    // =========================================================

    val metadata: String? = null,

    val note: String? = null
)