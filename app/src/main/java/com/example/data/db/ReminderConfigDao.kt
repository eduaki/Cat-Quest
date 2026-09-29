package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.ReminderConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderConfigDao {
    @Query("SELECT * FROM reminder_config WHERE id = 1 LIMIT 1")
    fun getConfig(): Flow<ReminderConfig?>

    @Query("SELECT * FROM reminder_config WHERE id = 1 LIMIT 1")
    suspend fun getConfigSync(): ReminderConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(config: ReminderConfig)

    @Update
    suspend fun update(config: ReminderConfig)
}
