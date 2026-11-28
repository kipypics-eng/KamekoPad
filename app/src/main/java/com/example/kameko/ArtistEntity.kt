package com.example.kameko

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "artists",
    indices = [Index(value = ["name"], unique = true)]
)
data class ArtistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

/** イベントの出演者（ラインナップ） */
@Entity(
    tableName = "event_artists",
    primaryKeys = ["eventId", "artistId"],
    foreignKeys = [
        ForeignKey(entity = EventEntity::class, parentColumns = ["id"], childColumns = ["eventId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ArtistEntity::class, parentColumns = ["id"], childColumns = ["artistId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["artistId"])]
)
data class EventArtistCrossRef(
    val eventId: Long,
    val artistId: Long
)

/** 写真の被写体アーティスト */
@Entity(
    tableName = "photo_artists",
    primaryKeys = ["photoId", "artistId"],
    foreignKeys = [
        ForeignKey(entity = PhotoEntity::class, parentColumns = ["id"], childColumns = ["photoId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ArtistEntity::class, parentColumns = ["id"], childColumns = ["artistId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["artistId"])]
)
data class PhotoArtistCrossRef(
    val photoId: Long,
    val artistId: Long
)

/** 絞り込み候補表示用（写真の枚数付き） */
data class ArtistWithPhotoCount(
    val id: Long,
    val name: String,
    val photoCount: Int
)

/** ギャラリーのアーティスト絞り込みで「未設定」を表す値 */
const val ARTIST_FILTER_UNASSIGNED = -1L
