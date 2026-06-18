package com.example.kameko

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import android.net.Uri

class XPostAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.packageName != "com.twitter.android") return

        // 投稿完了トーストやスナックバーのテキストを監視
        val text = event.text.joinToString()
        
        val isPostComplete = text.contains("ポストを送信しました") ||
                             text.contains("Your post was sent") ||
                             text.contains("Tweet sent") ||
                             text.contains("送信しました") ||
                             text.contains("Sent")

        if (isPostComplete) {
            Log.d("KamekoAccessibility", "Post complete detected: $text")
            handlePostCompleted()
        }
    }

    private fun handlePostCompleted() {
        val prefs = getSharedPreferences("kameko_pending", Context.MODE_PRIVATE)
        val pendingPaths = prefs.getStringSet("pendingPostPaths", emptySet()) ?: emptySet()
        
        serviceScope.launch {
            if (pendingPaths.isNotEmpty()) {
                // --- A. KamekoPadからの共有フロー ---
                Log.d("KamekoAccessibility", "Processing pending paths from Share flow")
                updatePhotosByPaths(pendingPaths)
                prefs.edit().remove("pendingPostPaths").apply()
            } else {
                // --- B. Xからの直接投稿フロー (推論検知) ---
                Log.d("KamekoAccessibility", "No pending paths. Trying heuristic detection for direct post...")
                val recentPaths = getRecentPhotosFromMediaStore()
                if (recentPaths.isNotEmpty()) {
                    Log.d("KamekoAccessibility", "Detected ${recentPaths.size} recent photos. Updating...")
                    updatePhotosByPaths(recentPaths.toSet())
                }
            }
        }
    }

    private suspend fun updatePhotosByPaths(paths: Set<String>) {
        try {
            val db = AppDatabase.getDatabase(applicationContext)
            val photoDao = db.photoDao()
            val historyDao = db.statusHistoryDao()
            
            val groupId = System.currentTimeMillis()
            val now = System.currentTimeMillis()

            paths.forEach { path ->
                val existing = photoDao.getPhotoByPath(path)
                if (existing != null) {
                    // 既に投稿済みの場合はスキップして重複記録を防ぐ
                    if (existing.status != PhotoStatus.POSTED.name) {
                        photoDao.updatePhoto(existing.copy(
                            status = PhotoStatus.POSTED.name,
                            sharedAt = now
                        ))
                        historyDao.insert(StatusHistoryEntity(
                            photoFilePath = path,
                            status = PhotoStatus.POSTED.name,
                            changedAt = now,
                            groupId = groupId
                        ))
                        Log.d("KamekoAccessibility", "Updated (Existing): $path")
                    }
                } else {
                    // DBにない（未スキャン）の写真は新しく登録
                    photoDao.insertPhoto(PhotoEntity(
                        filePath = path,
                        status = PhotoStatus.POSTED.name,
                        sharedAt = now
                    ))
                    historyDao.insert(StatusHistoryEntity(
                        photoFilePath = path,
                        status = PhotoStatus.POSTED.name,
                        changedAt = now,
                        groupId = groupId
                    ))
                    Log.d("KamekoAccessibility", "Updated (New Register): $path")
                }
            }
        } catch (e: Exception) {
            Log.e("KamekoAccessibility", "Failed to update photos", e)
        }
    }

    // 直近5分以内に作成・更新された写真を取得する
    private fun getRecentPhotosFromMediaStore(): List<String> {
        val paths = mutableListOf<String>()
        val fiveMinutesAgo = (System.currentTimeMillis() / 1000L) - (5 * 60)
        
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val selection = "${MediaStore.Images.Media.DATE_MODIFIED} >= ? OR ${MediaStore.Images.Media.DATE_ADDED} >= ?"
        val selectionArgs = arrayOf(fiveMinutesAgo.toString(), fiveMinutesAgo.toString())
        val sortOrder = "${MediaStore.Images.Media.DATE_MODIFIED} DESC LIMIT 4"

        try {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                while (cursor.moveToNext()) {
                    paths.add(cursor.getString(dataIndex))
                }
            }
        } catch (e: Exception) {
            Log.e("KamekoAccessibility", "MediaStore query failed", e)
        }
        return paths
    }

    override fun onInterrupt() {}
}
