package com.example.kameko

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ArtistDao {
    // ---- アーティスト ----
    @Query("SELECT * FROM artists ORDER BY name COLLATE NOCASE")
    abstract fun getAllArtists(): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM artists WHERE name = :name LIMIT 1")
    abstract suspend fun findByName(name: String): ArtistEntity?

    /** 既に同名がある場合は -1 を返す */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertArtist(artist: ArtistEntity): Long

    @Transaction
    open suspend fun getOrCreateArtistId(name: String): Long {
        val trimmed = name.trim()
        findByName(trimmed)?.let { return it.id }
        val id = insertArtist(ArtistEntity(name = trimmed))
        return if (id > 0) id else findByName(trimmed)!!.id
    }

    /** 同名が既にある場合は失敗（0件更新）にする */
    @Query("UPDATE OR IGNORE artists SET name = :name WHERE id = :id")
    abstract suspend fun renameArtist(id: Long, name: String): Int

    @Query("DELETE FROM artists WHERE id = :id")
    abstract suspend fun deleteArtist(id: Long)

    // ---- イベント出演者 ----
    @Query("""
        SELECT a.* FROM artists a
        INNER JOIN event_artists ea ON ea.artistId = a.id
        WHERE ea.eventId = :eventId
        ORDER BY a.name COLLATE NOCASE
    """)
    abstract fun getArtistsForEvent(eventId: Long): Flow<List<ArtistEntity>>

    @Query("""
        SELECT a.* FROM artists a
        INNER JOIN event_artists ea ON ea.artistId = a.id
        WHERE ea.eventId = :eventId
        ORDER BY a.name COLLATE NOCASE
    """)
    abstract suspend fun getArtistsForEventOnce(eventId: Long): List<ArtistEntity>

    @Query("SELECT * FROM event_artists")
    abstract fun getAllEventArtists(): Flow<List<EventArtistCrossRef>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertEventArtists(refs: List<EventArtistCrossRef>)

    @Query("DELETE FROM event_artists WHERE eventId = :eventId")
    abstract suspend fun clearEventArtists(eventId: Long)

    @Transaction
    open suspend fun setEventArtists(eventId: Long, artistIds: Collection<Long>) {
        clearEventArtists(eventId)
        insertEventArtists(artistIds.map { EventArtistCrossRef(eventId, it) })
    }

    // ---- 写真の被写体 ----
    @Query("""
        SELECT a.* FROM artists a
        INNER JOIN photo_artists pa ON pa.artistId = a.id
        WHERE pa.photoId = :photoId
        ORDER BY a.name COLLATE NOCASE
    """)
    abstract fun getArtistsForPhoto(photoId: Long): Flow<List<ArtistEntity>>

    @Query("SELECT * FROM photo_artists WHERE photoId IN (:photoIds)")
    abstract suspend fun getPhotoArtists(photoIds: List<Long>): List<PhotoArtistCrossRef>

    @Query("SELECT * FROM photo_artists WHERE photoId IN (:photoIds)")
    abstract fun getPhotoArtistsFlow(photoIds: List<Long>): Flow<List<PhotoArtistCrossRef>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertPhotoArtists(refs: List<PhotoArtistCrossRef>)

    @Query("DELETE FROM photo_artists WHERE photoId IN (:photoIds) AND artistId = :artistId")
    abstract suspend fun removeArtistFromPhotos(photoIds: List<Long>, artistId: Long)

    /** 絞り込み候補用。eventId 指定時はそのイベントの出演者のみ */
    @Query("""
        SELECT a.id, a.name,
            (SELECT COUNT(*) FROM photo_artists pa WHERE pa.artistId = a.id) AS photoCount
        FROM artists a
        WHERE :eventId IS NULL OR a.id IN (SELECT artistId FROM event_artists WHERE eventId = :eventId)
        ORDER BY a.name COLLATE NOCASE
    """)
    abstract fun getArtistsWithPhotoCount(eventId: Long?): Flow<List<ArtistWithPhotoCount>>

    /**
     * 出演者が1組だけのイベントに紐づいた写真へ、そのアーティストを自動付与する。
     * 既にアーティストが付いている写真は変更しない。
     */
    @Query("""
        INSERT OR IGNORE INTO photo_artists (photoId, artistId)
        SELECT p.id, ea.artistId FROM photos p
        INNER JOIN (
            SELECT eventId, MIN(artistId) AS artistId FROM event_artists
            GROUP BY eventId HAVING COUNT(*) = 1
        ) ea ON ea.eventId = p.eventId
        WHERE p.id IN (:photoIds)
        AND NOT EXISTS (SELECT 1 FROM photo_artists x WHERE x.photoId = p.id)
    """)
    abstract suspend fun autoAssignSingleArtist(photoIds: List<Long>)

    /** イベントの出演者を設定したとき、そのイベントの写真に自動付与する */
    @Query("""
        INSERT OR IGNORE INTO photo_artists (photoId, artistId)
        SELECT p.id, ea.artistId FROM photos p
        INNER JOIN (
            SELECT eventId, MIN(artistId) AS artistId FROM event_artists
            WHERE eventId = :eventId
            GROUP BY eventId HAVING COUNT(*) = 1
        ) ea ON ea.eventId = p.eventId
        WHERE NOT EXISTS (SELECT 1 FROM photo_artists x WHERE x.photoId = p.id)
    """)
    abstract suspend fun autoAssignSingleArtistForEvent(eventId: Long)
}
