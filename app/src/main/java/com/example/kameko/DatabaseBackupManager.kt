package com.example.kameko

import android.content.Context
import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * データベースファイルのバックアップと復元を担当するクラス
 */
class DatabaseBackupManager(private val context: Context) {

    private val dbName = "kameko_database"

    /**
     * 現在のデータベース（本体, -shm, -wal）をZIP圧縮してOutputStreamに書き出します。
     */
    fun backupDatabase(outputStream: OutputStream): Boolean {
        return try {
            // 1. データベース接続を閉じる
            AppDatabase.closeDatabase()

            val dbFile = context.getDatabasePath(dbName)
            val dbShm = File(dbFile.path + "-shm")
            val dbWal = File(dbFile.path + "-wal")

            ZipOutputStream(outputStream).use { zos ->
                val filesToBackup = listOf(dbFile, dbShm, dbWal)
                for (file in filesToBackup) {
                    if (file.exists()) {
                        val entry = ZipEntry(file.name)
                        zos.putNextEntry(entry)
                        file.inputStream().use { it.copyTo(zos) }
                        zos.closeEntry()
                    }
                }
            }
            true
        } catch (e: Exception) {
            Log.e("BackupManager", "Backup failed", e)
            false
        }
    }

    /**
     * ZIP形式のInputStreamからデータベースファイルを解凍し、現在のデータベースを上書きします。
     */
    fun restoreDatabase(inputStream: InputStream): Boolean {
        return try {
            // 1. 既存のデータベース接続を閉じる
            AppDatabase.closeDatabase()

            val dbFile = context.getDatabasePath(dbName)
            val dbDir = dbFile.parentFile ?: return false
            if (!dbDir.exists()) dbDir.mkdirs()

            ZipInputStream(inputStream).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val outFile = File(dbDir, entry.name)
                    // 確実に上書きするため、既存ファイルを削除
                    if (outFile.exists()) outFile.delete()
                    
                    FileOutputStream(outFile).use { fos ->
                        zis.copyTo(fos)
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            true
        } catch (e: Exception) {
            Log.e("BackupManager", "Restore failed", e)
            false
        }
    }
}
