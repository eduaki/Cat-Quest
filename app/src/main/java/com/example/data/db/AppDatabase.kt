package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.CatAffectionMessage
import com.example.data.models.CatMission
import com.example.data.models.CatProfile
import com.example.data.models.ReminderConfig

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE cat_profile ADD COLUMN totalScore INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}

        try {
            db.execSQL("ALTER TABLE cat_profile ADD COLUMN level INTEGER NOT NULL DEFAULT 1")
        } catch (_: Exception) {}

        try {
            db.execSQL("ALTER TABLE cat_missions ADD COLUMN pointsEarned INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}

        try {
            db.execSQL("ALTER TABLE cat_missions ADD COLUMN fishEarned INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS `cat_affection_messages` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `title` TEXT NOT NULL,
                    `text` TEXT NOT NULL,
                    `category` TEXT NOT NULL,
                    `isCustom` INTEGER NOT NULL,
                    `isEnabled` INTEGER NOT NULL
                )
            """.trimIndent())
        } catch (_: Exception) {}
    }
}

@Database(
    entities = [
        CatMission::class,
        CatProfile::class,
        ReminderConfig::class,
        CatAffectionMessage::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catMissionDao(): CatMissionDao
    abstract fun catProfileDao(): CatProfileDao
    abstract fun reminderConfigDao(): ReminderConfigDao
    abstract fun catAffectionMessageDao(): CatAffectionMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "miau_cat_care.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration(true)
                    .fallbackToDestructiveMigrationOnDowngrade(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
