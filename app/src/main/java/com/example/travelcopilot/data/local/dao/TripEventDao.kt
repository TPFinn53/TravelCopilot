package com.example.travelcopilot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.travelcopilot.data.local.entity.ChatMessageEntity
import com.example.travelcopilot.data.local.entity.TripEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripEventDao {

    @Insert
    suspend fun insert(event: TripEventEntity)
    @Query("SELECT * FROM trip_events WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun getEventsForTrip(tripId: Long): Flow<List<TripEventEntity>>

    @Query("SELECT * FROM trip_events WHERE tripId = :tripId ORDER BY timestamp ASC")
    suspend fun getEventsOnce(tripId: Long): List<TripEventEntity>

    @Query("SELECT * FROM trip_events WHERE tripId = :tripId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentEvents(tripId: Long, limit: Int = 50): List<TripEventEntity>

}