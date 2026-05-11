package com.example.travelcopilot.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.travelcopilot.data.local.Role

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: Long,
    val tripId: Long,
    val text: String,
    val role: Role,
    val timestamp: Long
)