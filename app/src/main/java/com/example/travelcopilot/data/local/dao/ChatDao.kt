package com.example.travelcopilot.data.local.dao

import com.example.travelcopilot.data.local.entity.ChatMessageEntity
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    // ➕ Insert message
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ChatMessageEntity)

    // 🔄 Reactive stream (used by UI)
    @Query("""
        SELECT * FROM chat_messages 
        WHERE tripId = :tripId 
        ORDER BY timestamp ASC
    """)
    fun getMessagesForTripFlow(tripId: Long): Flow<List<ChatMessageEntity>>

    // 📦 One-shot fetch (used by export, AI logic)
    @Query("""
        SELECT * FROM chat_messages 
        WHERE tripId = :tripId 
        ORDER BY timestamp ASC
    """)
    suspend fun getMessagesForTrip(tripId: Long): List<ChatMessageEntity>

    // ❌ Delete all messages for a trip
    @Query("DELETE FROM chat_messages WHERE tripId = :tripId")
    suspend fun deleteMessagesForTrip(tripId: Long)

    @Query("""
        UPDATE chat_messages
        SET text = :text
        WHERE id = :id
    """)
    suspend fun updateMessageContent(id: Long, text: String)

    @Query("SELECT * FROM chat_messages WHERE tripId = :tripId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentMessages(
        tripId: Long,
        limit: Int = 50
    ): List<ChatMessageEntity>
}