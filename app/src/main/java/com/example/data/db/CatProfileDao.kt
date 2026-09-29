package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.CatProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface CatProfileDao {
    @Query("SELECT * FROM cat_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<CatProfile?>

    @Query("SELECT * FROM cat_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfileSync(): CatProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: CatProfile)

    @Update
    suspend fun update(profile: CatProfile)
}
