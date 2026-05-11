package com.example.travelcopilot.core.context

import com.example.travelcopilot.domain.model.ChatMessage
import com.example.travelcopilot.domain.model.TripState
import com.example.travelcopilot.data.local.entity.TripEventEntity
import com.example.travelcopilot.utils.location.LocationData

data class ContextSnapshot(
    val trip: TripContext,
    val location: LocationContext,
    val time: TimeContext,
    val memory: MemoryContext,
    val signals: SignalContext
)