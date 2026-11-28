package com.example.kameko

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos ORDER BY id DESC")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos ORDER BY id DESC")
    fun getAllPhotosPaging(): PagingSource<Int, PhotoEntity>

    @Query("""
        SELECT * FROM photos 
        WHERE (:status = 'ALL' OR status = :status)
        AND (:type = 'ALL' OR fileType = :type)
        AND (:eventId IS NULL OR eventId = :eventId)
        AND (:make IS NULL OR cameraMake = :make)
        AND (
            :artistId IS NULL
            OR (:artistId = -1 AND NOT EXISTS (SELECT 1 FROM photo_artists pa WHERE pa.photoId = photos.id))
            OR EXISTS (SELECT 1 FROM photo_artists pa WHERE pa.photoId = photos.id AND pa.artistId = :artistId)
        )
        ORDER BY 
            CASE :sortType
                WHEN 'DATE_ADDED' THEN dateAdded
                WHEN 'DATE_TAKEN' THEN COALESCE(dateTaken, dateAdded)
                WHEN 'DATE_MODIFIED' THEN COALESCE(dateModified, dateAdded)
                ELSE dateAdded
            END DESC,
            id DESC
    """)
    fun getFilteredPhotosPaging(
        status: String,
        type: String,
        eventId: Long?,
        make: String?,
        artistId: Long?,
        sortType: String
    ): PagingSource<Int, PhotoEntity>

    @Query("SELECT * FROM photos")
    suspend fun getAllPhotosOnce(): List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>): List<Long>

    @Update
    suspend fun updatePhoto(photo: PhotoEntity)

    @Update
    suspend fun updatePhotos(photos: List<PhotoEntity>)

    @Query("SELECT * FROM photos WHERE filePath = :path LIMIT 1")
    suspend fun getPhotoByPath(path: String): PhotoEntity?

    @Query("SELECT * FROM photos WHERE eventId = :eventId")
    suspend fun getPhotosByEventId(eventId: Long): List<PhotoEntity>

    @Query("SELECT DISTINCT cameraMake FROM photos WHERE cameraMake IS NOT NULL")
    fun getUniqueMakes(): Flow<List<String>>

    @Query("UPDATE photos SET cameraMake = NULL")
    suspend fun clearAllCameraMakes()

    @Query("UPDATE photos SET cameraMake = NULL WHERE cameraMake = :make")
    suspend fun clearCameraMake(make: String)

    /**
     * 指定した接頭辞を新しい接頭辞に一括置換します。
     * SQLiteのreplace関数を使用し、パスの不整合を修正します。
     */
    @Query("UPDATE photos SET filePath = :newPrefix || SUBSTR(filePath, LENGTH(:oldPrefix) + 1) WHERE filePath LIKE :oldPrefix || '%'")
    suspend fun replaceFilePathPrefix(oldPrefix: String, newPrefix: String)

    @Query("SELECT * FROM photos WHERE filePath NOT IN (:currentPaths)")
    suspend fun getOrphanedPhotos(currentPaths: List<String>): List<PhotoEntity>
}