package com.example.travelcopilot.data.repository

import com.example.travelcopilot.data.local.dao.TripEventDao
import com.example.travelcopilot.data.local.entity.TripEventEntity
import com.example.travelcopilot.data.local.mapper.toTripEvent
import com.example.travelcopilot.domain.model.TripEvent
import com.example.travelcopilot.domain.model.TripEventType
import javax.inject.Inject

class TripEventRepository @Inject constructor(
    private val tripEventDao: TripEventDao
) {

    // 🔄 STREAM (for UI if needed)
    fun getEventsForTrip(tripId: Long) =
        tripEventDao.getEventsForTrip(tripId)

    // ⚡ ONE-SHOT (used by ContextProvider / analytics)
    suspend fun getRecentEvents(tripId: Long, limit: Int = 50): List<TripEvent> {
        return tripEventDao.getRecentEvents(tripId, limit).map { it.toTripEvent() }
    }

    // ➕ LOG GENERIC EVENT
    suspend fun logEvent(
        tripId: Long,
        type: TripEventType,
        data: String? = null
    ) {
        tripEventDao.insert(
            TripEventEntity(
                id = System.currentTimeMillis(),
                tripId = tripId,
                type = type,
                note = data ?: "",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    // 🚗 STATE EVENTS (optional helpers — recommended)

    // Lifecycle
    suspend fun logTripCreated(tripId: Long) =
        logEvent(tripId, TripEventType.TRIP_CREATED)

    suspend fun logTripStart(tripId: Long) =
        logEvent(tripId, TripEventType.TRIP_STARTED)

    suspend fun logTripStopped(tripId: Long) =
        logEvent(tripId, TripEventType.TRIP_STOPPED)

    suspend fun logTripCompleted(tripId: Long) =
        logEvent(tripId, TripEventType.TRIP_COMPLETED)

    // Movement
    suspend fun logDriving(tripId: Long) =
        logEvent(tripId, TripEventType.DRIVE_STARTED)

    suspend fun logIdling(tripId: Long) =
        logEvent(tripId, TripEventType.IDLE_DETECTED)

    suspend fun logParked(tripId: Long) =
        logEvent(tripId, TripEventType.PARKED)

    // Navigation
    suspend fun logNavigating(tripId: Long) =
        logEvent(tripId, TripEventType.NAVIGATION_STARTED)

    suspend fun logArrival(tripId: Long) =
        logEvent(tripId, TripEventType.DESTINATION_ARRIVED)

    // Journey moments
    suspend fun logFuelStop(tripId: Long) =
        logEvent(tripId, TripEventType.FUEL_STOP)

    suspend fun logFoodStop(tripId: Long) =
        logEvent(tripId, TripEventType.FOOD_STOP)

    suspend fun logScenicStop(tripId: Long) =
        logEvent(tripId, TripEventType.SCENIC_STOP)

    // System
    suspend fun logSystemError(tripId: Long) =
        logEvent(tripId, TripEventType.ERROR)

    suspend fun logSystemWarning(tripId: Long) =
        logEvent(tripId, TripEventType.WARNING)

    // Media
    suspend fun logPhotoCaptured(tripId: Long) =
        logEvent(tripId, TripEventType.PHOTO_CAPTURED)

    // User interactions
    suspend fun logUserNote(
        tripId: Long,
        text: String
    ) {
        logEvent(
            tripId = tripId,
            type = TripEventType.USER_NOTE,
            data = text
        )
    }
    suspend fun logVoiceNote(
        tripId: Long,
        text: String
    ) {
        logEvent(
            tripId = tripId,
            type = TripEventType.VOICE_NOTE,
            data = text
        )
    }

    // AI interactions
    suspend fun logUserMessage(
        tripId: Long,
        text: String
    ) {
        logEvent(
            tripId = tripId,
            type = TripEventType.USER_MESSAGE,
            data = text
        )
    }
    suspend fun logAiMessage(
        tripId: Long,
        text: String
    ) {
        logEvent(
            tripId = tripId,
            type = TripEventType.AI_MESSAGE,
            data = text
        )
    }
}