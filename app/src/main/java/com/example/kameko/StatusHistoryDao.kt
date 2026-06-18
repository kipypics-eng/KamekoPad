package com.example.kameko

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: StatusHistoryEntity)

    @Query("""
        SELECT COUNT(*) FROM status_history 
        WHERE status = 'POSTED' 
        AND changedAt >= :since
    """)
    fun getPostedPhotoCountSince(since: Long): Flow<Int>

    @Query("""
        SELECT COUNT(DISTINCT groupId) FROM status_history 
        WHERE status = 'POSTED' 
        AND changedAt >= :since
    """)
    fun getUniquePostCountSince(since: Long): Flow<Int>

    @Query("SELECT * FROM status_history ORDER BY changedAt DESC")
    fun getAllHistory(): Flow<List<StatusHistoryEntity>>

    // 全投稿履歴（新しい順）
    @Query("""
        SELECT * FROM status_history 
        WHERE status = 'POSTED'
        ORDER BY changedAt DESC
    """)
    fun getAllPostedHistory(): Flow<List<StatusHistoryEntity>>

    // 写真単位の履歴
    @Query("""
        SELECT * FROM status_history 
        WHERE photoFilePath = :filePath 
        ORDER BY changedAt DESC
    """)
    fun getHistoryByPhoto(filePath: String): Flow<List<StatusHistoryEntity>>

    // イベント単位：該当写真パスリストの履歴
    @Query("""
        SELECT * FROM status_history 
        WHERE photoFilePath IN (:filePaths)
        AND status = 'POSTED'
        ORDER BY changedAt DESC
    """)
    fun getPostedHistoryByPaths(filePaths: List<String>): Flow<List<StatusHistoryEntity>>

    // 指定されたグループIDの履歴を削除
    @Query("DELETE FROM status_history WHERE groupId = :groupId")
    suspend fun deleteHistoryGroup(groupId: Long)

    // 指定されたグループIDの履歴を取得
    @Query("SELECT * FROM status_history WHERE groupId = :groupId")
    suspend fun getHistoryByGroupId(groupId: Long): List<StatusHistoryEntity>

    // 手動で直近の履歴を1投稿（グループ単位）削除（カウンター減算用）
    @Query("""
        DELETE FROM status_history 
        WHERE groupId = (
            SELECT groupId FROM status_history 
            WHERE status = 'POSTED' 
            AND changedAt >= :since
            ORDER BY changedAt DESC 
            LIMIT 1
        )
    """)
    suspend fun deleteLatestGroupPostedSince(since: Long)

    @Query("UPDATE status_history SET photoFilePath = :newPrefix || SUBSTR(photoFilePath, LENGTH(:oldPrefix) + 1) WHERE photoFilePath LIKE :oldPrefix || '%'")
    suspend fun replaceFilePathPrefix(oldPrefix: String, newPrefix: String)

    @Query("UPDATE status_history SET photoFilePath = :newPath WHERE photoFilePath = :oldPath")
    suspend fun updatePath(oldPath: String, newPath: String)
}