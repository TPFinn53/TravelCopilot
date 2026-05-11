package com.example.travelcopilot.data.local.mapper

import com.example.travelcopilot.data.local.entity.TripEventEntity
import com.example.travelcopilot.domain.model.TripEvent
import com.example.travelcopilot.domain.model.TripEventType

// =============================================================
// Entity -> Domain
// =============================================================

fun TripEventEntity.toTripEvent(): TripEvent{

    return when (type) {

        // =====================================================
        // Lifecycle
        // =====================================================

        TripEventType.TRIP_CREATED -> {

            TripEvent.TripCreated(
                tripId = tripId,
                timestamp = timestamp
            )
        }

        TripEventType.TRIP_STARTED -> {

            TripEvent.TripStarted(
                tripId = tripId,
                timestamp = timestamp
            )
        }

        TripEventType.TRIP_STOPPED -> {

            TripEvent.TripStopped(
                tripId = tripId,
                timestamp = timestamp
            )
        }

        TripEventType.TRIP_COMPLETED -> {

            TripEvent.TripCompleted(
                tripId = tripId,
                timestamp = timestamp
            )
        }

        // =====================================================
        // Movement
        // =====================================================

        TripEventType.DRIVE_STARTED -> {

            TripEvent.DrivingDetected(
                tripId = tripId,
                timestamp = timestamp,
                speedMps = speedMps ?: 0f
            )
        }

        TripEventType.IDLE_DETECTED -> {

            TripEvent.IdleDetected(
                tripId = tripId,
                timestamp = timestamp,
                durationMs = durationMs ?: 0L
            )
        }

        TripEventType.PARKED -> {

            TripEvent.Parked(
                tripId = tripId,
                timestamp = timestamp
            )
        }

        // =====================================================
        // Navigation
        // =====================================================

        TripEventType.NAVIGATION_STARTED -> {

            TripEvent.NavigationStarted(
                tripId = tripId,
                timestamp = timestamp,
                destinationName = metadata
            )
        }

        TripEventType.DESTINATION_ARRIVED -> {

            TripEvent.DestinationArrived(
                tripId = tripId,
                timestamp = timestamp,
                destinationName = metadata
            )
        }

        // =====================================================
        // Journey moments
        // =====================================================

        TripEventType.FUEL_STOP -> {

            TripEvent.FuelStop(
                tripId = tripId,
                timestamp = timestamp,
                locationName = locationName
            )
        }

        TripEventType.REST_STOP -> {

            TripEvent.RestStop(
                tripId = tripId,
                timestamp = timestamp,
                locationName = locationName
            )
        }

        TripEventType.FOOD_STOP -> {

            TripEvent.FoodStop(
                tripId = tripId,
                timestamp = timestamp,
                locationName = locationName
            )
        }

        TripEventType.SCENIC_STOP -> {

            TripEvent.ScenicStop(
                tripId = tripId,
                timestamp = timestamp,
                locationName = locationName
            )
        }

        // =====================================================
        // User interactions
        // =====================================================

        TripEventType.USER_NOTE -> {

            TripEvent.UserNote(
                tripId = tripId,
                timestamp = timestamp,
                text = message ?: ""
            )
        }

        TripEventType.VOICE_NOTE -> {

            TripEvent.VoiceNote(
                tripId = tripId,
                timestamp = timestamp,
                transcript = message ?: ""
            )
        }

        // =====================================================
        // AI interactions
        // =====================================================

        TripEventType.USER_MESSAGE -> {

            TripEvent.UserMessage(
                tripId = tripId,
                timestamp = timestamp,
                text = message ?: ""
            )
        }

        TripEventType.AI_MESSAGE -> {

            TripEvent.AiMessage(
                tripId = tripId,
                timestamp = timestamp,
                text = message ?: ""
            )
        }

        // =====================================================
        // Media
        // =====================================================

        TripEventType.PHOTO_CAPTURED -> {

            TripEvent.PhotoCaptured(
                tripId = tripId,
                timestamp = timestamp,
                uri = uri ?: ""
            )
        }

        // =====================================================
        // System
        // =====================================================

        TripEventType.ERROR -> {

            TripEvent.Error(
                tripId = tripId,
                timestamp = timestamp,
                message = message ?: ""
            )
        }

        TripEventType.WARNING -> {

            TripEvent.Warning(
                tripId = tripId,
                timestamp = timestamp,
                message = message ?: ""
            )
        }
    }
}

// =============================================================
// Domain -> Entity
// =============================================================

fun TripEvent.toEntity(): TripEventEntity{

    return when (this) {

        // =====================================================
        // Lifecycle
        // =====================================================

        is TripEvent.TripCreated -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.TRIP_CREATED,
                timestamp = timestamp
            )
        }

        is TripEvent.TripStarted -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.TRIP_STARTED,
                timestamp = timestamp
            )
        }

        is TripEvent.TripStopped -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.TRIP_STOPPED,
                timestamp = timestamp
            )
        }

        is TripEvent.TripCompleted -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.TRIP_COMPLETED,
                timestamp = timestamp
            )
        }

        // =====================================================
        // Movement
        // =====================================================

        is TripEvent.DrivingDetected -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.DRIVE_STARTED,
                timestamp = timestamp,
                speedMps = speedMps
            )
        }

        is TripEvent.IdleDetected -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.IDLE_DETECTED,
                timestamp = timestamp,
                durationMs = durationMs
            )
        }

        is TripEvent.Parked -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.PARKED,
                timestamp = timestamp
            )
        }

        // =====================================================
        // Navigation
        // =====================================================

        is TripEvent.NavigationStarted -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.NAVIGATION_STARTED,
                timestamp = timestamp,
                metadata = destinationName
            )
        }

        is TripEvent.DestinationArrived -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.DESTINATION_ARRIVED,
                timestamp = timestamp,
                metadata = destinationName
            )
        }

        // =====================================================
        // Journey moments
        // =====================================================

        is TripEvent.FuelStop -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.FUEL_STOP,
                timestamp = timestamp,
                locationName = locationName
            )
        }

        is TripEvent.RestStop -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.REST_STOP,
                timestamp = timestamp,
                locationName = locationName
            )
        }

        is TripEvent.FoodStop -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.FOOD_STOP,
                timestamp = timestamp,
                locationName = locationName
            )
        }

        is TripEvent.ScenicStop -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.SCENIC_STOP,
                timestamp = timestamp,
                locationName = locationName
            )
        }

        // =====================================================
        // User interactions
        // =====================================================

        is TripEvent.UserNote -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.USER_NOTE,
                timestamp = timestamp,
                message = text
            )
        }

        is TripEvent.VoiceNote -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.VOICE_NOTE,
                timestamp = timestamp,
                message = transcript
            )
        }

        // =====================================================
        // AI interactions
        // =====================================================

        is TripEvent.UserMessage -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.USER_MESSAGE,
                timestamp = timestamp,
                message = text
            )
        }

        is TripEvent.AiMessage -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.AI_MESSAGE,
                timestamp = timestamp,
                message = text
            )
        }

        // =====================================================
        // Media
        // =====================================================

        is TripEvent.PhotoCaptured -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.PHOTO_CAPTURED,
                timestamp = timestamp,
                uri = uri
            )
        }

        // =====================================================
        // System
        // =====================================================

        is TripEvent.Error -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.ERROR,
                timestamp = timestamp,
                message = message
            )
        }

        is TripEvent.Warning -> {

            TripEventEntity(
                tripId = tripId,
                type = TripEventType.WARNING,
                timestamp = timestamp,
                message = message
            )
        }
    }
}