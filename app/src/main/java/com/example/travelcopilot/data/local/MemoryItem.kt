package com.example.travelcopilot.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_items")
data class MemoryItem(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val content: String,

    val timestamp: Long
)
