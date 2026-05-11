package com.example.travelcopilot.core.model

import com.example.travelcopilot.domain.model.ChatMessage
import com.example.travelcopilot.domain.model.TripEvent

sealed class TimelineItem {

    data class EventItem(
        val event: TripEvent
    ) : TimelineItem()

    data class ChatItem(
        val message: ChatMessage
    ) : TimelineItem()

    data class SystemItem(
        val message: String,
        val timestamp: Long
    ) : TimelineItem()
}