package com.example.travelcopilot.domain.model

sealed class TripEvent {

    abstract val timestamp: Long

    // =========================================================
    // Trip lifecycle
    // =========================================================

    data class TripCreated(
        override val timestamp: Long,
        val tripId: Long
    ) : TripEvent()

    data class TripStarted(
        override val timestamp: Long,
        val tripId: Long
    ) : TripEvent()

    data class TripStopped(
        override val timestamp: Long,
        val tripId: Long
    ) : TripEvent()

    data class TripCompleted(
        override val timestamp: Long,
        val tripId: Long
    ) : TripEvent()

    // =========================================================
    // Movement
    // =========================================================

    data class DrivingDetected(
        override val timestamp: Long,
        val tripId: Long,
        val speedMps: Float
    ) : TripEvent()

    data class IdleDetected(
        override val timestamp: Long,
        val tripId: Long,
        val durationMs: Long
    ) : TripEvent()

    data class Parked(
        override val timestamp: Long,
        val tripId: Long
    ) : TripEvent()

    // =========================================================
    // Navigation
    // =========================================================

    data class NavigationStarted(
        override val timestamp: Long,
        val tripId: Long,
        val destinationName: String?
    ) : TripEvent()

    data class DestinationArrived(
        override val timestamp: Long,
        val tripId: Long,
        val destinationName: String?
    ) : TripEvent()

    // =========================================================
    // Journey moments
    // =========================================================

    data class FuelStop(
        override val timestamp: Long,
        val locationName: String?,
        val tripId: Long
    ) : TripEvent()

    data class RestStop(
        override val timestamp: Long,
        val locationName: String?,
        val tripId: Long
    ) : TripEvent()

    data class ScenicStop(
        override val timestamp: Long,
        val locationName: String?,
        val tripId: Long
    ) : TripEvent()

    data class FoodStop(
        override val timestamp: Long,
        val locationName: String?,
        val tripId: Long
    ) : TripEvent()

    // =========================================================
    // Notes & media
    // =========================================================

    data class UserNote(
        override val timestamp: Long,
        val text: String,
        val tripId: Long
    ) : TripEvent()

    data class VoiceNote(
        override val timestamp: Long,
        val tripId: Long,
        val transcript: String
    ) : TripEvent()

    data class PhotoCaptured(
        override val timestamp: Long,
        val tripId: Long,
        val uri: String
    ) : TripEvent()

    // =========================================================
    // Chat
    // =========================================================

    data class UserMessage(
        override val timestamp: Long,
        val tripId: Long,
        val text: String
    ) : TripEvent()

    data class AiMessage(
        override val timestamp: Long,
        val tripId: Long,
        val text: String
    ) : TripEvent()

    // =========================================================
    // System
    // =========================================================

    data class Warning(
        override val timestamp: Long,
        val tripId: Long,
        val message: String
    ) : TripEvent()

    data class Error(
        override val timestamp: Long,
        val tripId: Long,
        val message: String
    ) : TripEvent()
}