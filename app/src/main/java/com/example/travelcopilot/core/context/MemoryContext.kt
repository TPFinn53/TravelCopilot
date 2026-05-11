package com.example.travelcopilot.core.context

import com.example.travelcopilot.domain.model.ChatMessage
import com.example.travelcopilot.domain.model.TripEvent

data class MemoryContext(
    val recentEvents: List<TripEvent>,
    val recentMessages: List<ChatMessage>,
    val lastAiResponseTime: Long?
)
