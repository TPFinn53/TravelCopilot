package com.example.travelcopilot.domain.model

import com.example.travelcopilot.data.local.Role

data class ChatMessage(
    val id: Long = 0,
    val tripId: Long,

    val role: Role,

    val content: String,
    val timestamp: Long,

    // future-ready
    val isVoice: Boolean = false
)