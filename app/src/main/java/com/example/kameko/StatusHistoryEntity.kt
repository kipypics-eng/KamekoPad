package com.example.kameko

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "status_history")
data class StatusHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val photoFilePath: String,
    val status: String,
    val changedAt: Long = System.currentTimeMillis(),
    val groupId: Long = 0 // 同時投稿をグルーピングするためのID
)