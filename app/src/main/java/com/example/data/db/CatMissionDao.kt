package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.CatMission
import kotlinx.coroutines.flow.Flow

@Dao
interface CatMissionDao {
    @Query("SELECT * FROM cat_missions WHERE date = :date ORDER BY id ASC")
    fun getMissionsForDate(date: String): Flow<List<CatMission>>

    @Query("SELECT * FROM cat_missions WHERE date LIKE :monthPattern ORDER BY date ASC, id ASC")
    fun getMissionsForMonth(monthPattern: String): Flow<List<CatMission>>

    @Query("SELECT * FROM cat_missions WHERE date LIKE :monthPattern AND isCompleted = 1 AND photoPath IS NOT NULL ORDER BY completedAt ASC")
    fun getPhotosForMonth(monthPattern: String): Flow<List<CatMission>>

    @Query("SELECT * FROM cat_missions WHERE isCompleted = 1 AND photoPath IS NOT NULL ORDER BY date DESC, completedAt DESC")
    fun getAllPhotos(): Flow<List<CatMission>>

    @Query("SELECT * FROM cat_missions WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, id ASC")
    fun getMissionsBetween(startDate: String, endDate: String): Flow<List<CatMission>>

    @Query("SELECT DISTINCT date FROM cat_missions WHERE isCompleted = 1")
    fun getDatesWithCompletedMissions(): Flow<List<String>>

    @Query("SELECT * FROM cat_missions WHERE id = :id LIMIT 1")
    suspend fun getMissionById(id: Long): CatMission?

    @Query("SELECT COUNT(*) FROM cat_missions WHERE date = :date")
    suspend fun countMissionsForDate(date: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: CatMission): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<CatMission>)

    @Update
    suspend fun updateMission(mission: CatMission)

    @Delete
    suspend fun deleteMission(mission: CatMission)

    @Query("UPDATE cat_missions SET isCompleted = :isCompleted, photoPath = :photoPath, completedAt = :completedAt, catComment = :comment, catMood = :mood, pointsEarned = :points, fishEarned = :fish WHERE id = :id")
    suspend fun completeMission(
        id: Long,
        isCompleted: Boolean,
        photoPath: String?,
        completedAt: Long?,
        comment: String,
        mood: String,
        points: Int = 0,
        fish: Int = 0
    )
}
