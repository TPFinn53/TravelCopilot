package com.example.travelcopilot.data.local

import com.example.travelcopilot.data.local.dao.MemoryDao
import kotlinx.coroutines.flow.Flow

class MemoryStore(
    private val memoryDao: MemoryDao
) {

    fun getMemories(): Flow<List<MemoryItem>> {
        return memoryDao.getAllMemories()
    }

    suspend fun addMemory(content: String) {
        val memory = MemoryItem(
            content = content,
            timestamp = System.currentTimeMillis()
        )
        memoryDao.insert(memory)
    }

    suspend fun clear() {
        memoryDao.clearAll()
    }
}