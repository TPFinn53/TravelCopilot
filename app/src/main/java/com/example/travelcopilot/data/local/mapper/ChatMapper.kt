package com.example.travelcopilot.data.local.mapper

import com.example.travelcopilot.domain.model.ChatMessage
import com.example.travelcopilot.data.local.entity.ChatMessageEntity

fun ChatMessageEntity.toChatMessage(): ChatMessage {
    return ChatMessage(
        id = id,
        tripId = tripId,
        role = role,
        content = text,
        timestamp = timestamp
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = id,
        tripId = tripId,
        role = role,
        text = content,
        timestamp = timestamp
    )
}