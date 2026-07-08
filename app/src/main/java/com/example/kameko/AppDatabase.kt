package com.example.kameko

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        PhotoEntity::class,
        EventEntity::class,
        StatusHistoryEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun eventDao(): EventDao
    abstract fun statusHistoryDao(): StatusHistoryDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE photos ADD COLUMN dateAdded INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE photos ADD COLUMN dateTaken INTEGER")
                db.execSQL("ALTER TABLE photos ADD COLUMN dateModified INTEGER")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `status_history` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `photoFilePath` TEXT NOT NULL,
                        `status` TEXT NOT NULL,
                        `changedAt` INTEGER NOT NULL
                    )
                """)
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE status_history ADD COLUMN groupId INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE photos ADD COLUMN cameraMake TEXT")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE photos ADD COLUMN iso INTEGER")
                db.execSQL("ALTER TABLE photos ADD COLUMN focalLength REAL")
                db.execSQL("ALTER TABLE photos ADD COLUMN aperture REAL")
                db.execSQL("ALTER TABLE photos ADD COLUMN shutterSpeed REAL")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kameko_database"
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build().also { INSTANCE = it }
            }
        }

        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
