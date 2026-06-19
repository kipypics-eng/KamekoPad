package com.example.kameko

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY id DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity): Long

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: Long): EventEntity?

    @Query("SELECT * FROM events")
    suspend fun getAllEventsOnce(): List<EventEntity>

    @Query("SELECT * FROM events WHERE name = :name AND venue = :venue AND eventDate = :date LIMIT 1")
    suspend fun findEvent(name: String, venue: String, date: String): EventEntity?

    @androidx.room.Delete
    suspend fun delete(event: EventEntity)
}