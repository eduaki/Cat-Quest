package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.CatAffectionMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface CatAffectionMessageDao {
    @Query("SELECT * FROM cat_affection_messages ORDER BY isCustom DESC, id ASC")
    fun getAllMessages(): Flow<List<CatAffectionMessage>>

    @Query("SELECT * FROM cat_affection_messages WHERE isEnabled = 1")
    suspend fun getEnabledMessagesSync(): List<CatAffectionMessage>

    @Query("SELECT COUNT(*) FROM cat_affection_messages")
    suspend fun countMessages(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: CatAffectionMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<CatAffectionMessage>)

    @Update
    suspend fun updateMessage(message: CatAffectionMessage)

    @Delete
    suspend fun deleteMessage(message: CatAffectionMessage)
}
