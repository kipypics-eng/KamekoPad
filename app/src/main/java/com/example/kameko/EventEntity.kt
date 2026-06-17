package com.example.kameko

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val venue: String,
    val eventDate: String = "",
    val startTime: Long = 0L,
    val endTime: Long = 0L
)