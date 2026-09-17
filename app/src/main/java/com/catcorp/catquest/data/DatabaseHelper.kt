package com.catcorp.catquest.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "catquest.db"
        private const val DATABASE_VERSION = 1
        
        const val TABLE_TASKS = "tasks"
        const val COLUMN_ID = "id"
        const val COLUMN_DATE = "date"
        const val COLUMN_CHALLENGE = "challenge_title"
        const val COLUMN_PHOTO_URI = "photo_uri"
        const val COLUMN_COMPLETED = "is_completed"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DATE TEXT UNIQUE NOT NULL,
                $COLUMN_CHALLENGE TEXT NOT NULL,
                $COLUMN_PHOTO_URI TEXT,
                $COLUMN_COMPLETED INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    fun insertOrUpdateTask(entry: TaskEntry): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DATE, entry.date)
            put(COLUMN_CHALLENGE, entry.challengeTitle)
            put(COLUMN_PHOTO_URI, entry.photoUri)
            put(COLUMN_COMPLETED, if (entry.isCompleted) 1 else 0)
        }
        return db.insertWithOnConflict(
            TABLE_TASKS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun getTaskByDate(date: String): TaskEntry? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_TASKS,
            null,
            "$COLUMN_DATE = ?",
            arrayOf(date),
            null,
            null,
            null
        )

        var task: TaskEntry? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val challenge = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHALLENGE))
            val photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI))
            val completed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1
            task = TaskEntry(id, date, challenge, photoUri, completed)
        }
        cursor.close()
        return task
    }

    fun getTasksForMonth(yearMonth: String): List<TaskEntry> {
        val db = this.readableDatabase
        val tasks = mutableListOf<TaskEntry>()
        val cursor = db.query(
            TABLE_TASKS,
            null,
            "$COLUMN_DATE LIKE ?",
            arrayOf("$yearMonth-%"),
            null,
            null,
            "$COLUMN_DATE ASC"
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val challenge = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CHALLENGE))
                val photoUri = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PHOTO_URI))
                val completed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1
                tasks.add(TaskEntry(id, date, challenge, photoUri, completed))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tasks
    }
}
