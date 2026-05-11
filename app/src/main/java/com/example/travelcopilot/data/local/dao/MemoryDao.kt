package com.example.travelcopilot.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travelcopilot.data.local.MemoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {

    @Query("SELECT * FROM memory_items ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<MemoryItem>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(memory: MemoryItem)

    @Query("DELETE FROM memory_items")
    suspend fun clearAll()
}