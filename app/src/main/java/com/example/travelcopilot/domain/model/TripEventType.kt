package com.example.travelcopilot.domain.model

enum class TripEventType {

    // Lifecycle
    TRIP_CREATED,
    TRIP_STARTED,
    TRIP_STOPPED,
    TRIP_COMPLETED,

    // Movement
    DRIVE_STARTED,
    IDLE_DETECTED,
    PARKED,

    // Navigation
    NAVIGATION_STARTED,
    DESTINATION_ARRIVED,

    // Journey moments
    FUEL_STOP,
    REST_STOP,
    FOOD_STOP,
    SCENIC_STOP,

    // User interactions
    USER_NOTE,
    VOICE_NOTE,

    // AI interactions
    USER_MESSAGE,
    AI_MESSAGE,

    // Media
    PHOTO_CAPTURED,

    // System
    ERROR,
    WARNING
}