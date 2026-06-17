package com.example.kameko

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos ORDER BY id DESC")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM photos")
    suspend fun getAllPhotosOnce(): List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<PhotoEntity>)

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
}