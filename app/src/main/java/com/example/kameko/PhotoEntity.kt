package com.example.kameko

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val filePath: String,
    val fileType: String = "JPEG", // "JPEG" or "RAW"
    val status: String = PhotoStatus.SHOT.name,
    val eventId: Long? = null,
    val memo: String? = null,
    val sharedAt: Long? = null,
    val cameraMake: String? = null,
    val iso: Int? = null,
    val focalLength: Double? = null,
    val aperture: Double? = null,
    val shutterSpeed: Double? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val dateTaken: Long? = null,
    val dateModified: Long? = null
)

enum class PhotoStatus {
    SHOT,       // 無印
    FAVORITE,   // FAVORITE
    POSTED      // POSTED
}

// サムネイルや詳細画面で使う簡易ラベル
fun PhotoStatus.label(): String = when (this) {
    PhotoStatus.SHOT      -> "📷"
    PhotoStatus.FAVORITE  -> "⭐"
    PhotoStatus.POSTED    -> "🟢"
}

// 次のステータスへ進むトグルロジック（3段階に変更）
fun PhotoStatus.next(): PhotoStatus = when (this) {
    PhotoStatus.SHOT      -> PhotoStatus.FAVORITE
    PhotoStatus.FAVORITE  -> PhotoStatus.POSTED
    PhotoStatus.POSTED    -> PhotoStatus.SHOT
}