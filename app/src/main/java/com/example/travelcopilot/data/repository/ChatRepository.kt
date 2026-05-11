package com.example.travelcopilot.data.repository

import com.example.travelcopilot.data.local.dao.ChatDao
import com.example.travelcopilot.data.local.mapper.toChatMessage
import com.example.travelcopilot.data.local.mapper.toEntity
import com.example.travelcopilot.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val chatDao: ChatDao
) {

    // 🔄 Reactive stream for UI
    fun getMessages(tripId: Long): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForTripFlow(tripId).map { entities ->
            entities.map { it.toChatMessage() }
        }
    }

    // 📦 One-shot fetch for export / backup
    suspend fun getMessagesOnce(tripId: Long): List<ChatMessage> {
        return chatDao.getMessagesForTrip(tripId).map { it.toChatMessage() }
    }

    // ➕ Insert message
    suspend fun insert(chat: ChatMessage) {
        chatDao.insert(chat.toEntity())
    }

    // ❌ Optional: clear messages for a trip
    suspend fun deleteMessagesForTrip(tripId: Long) {
        chatDao.deleteMessagesForTrip(tripId)
    }

    suspend fun updateMessageContent(id: Long, text: String) {
        chatDao.updateMessageContent(id, text)
    }
    suspend fun getRecentMessages(tripId: Long, limit: Int = 50): List<ChatMessage> {
        return chatDao.getRecentMessages(tripId, limit).map { it.toChatMessage() }
    }

}