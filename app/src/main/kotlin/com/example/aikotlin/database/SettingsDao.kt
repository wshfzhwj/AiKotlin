package com.example.aikotlin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.aikotlin.model.Settings
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

    @Insert
    suspend fun insertSetting(settings: Settings)

    @Query("SELECT * FROM settings ORDER BY id DESC")
    fun getAllSettings(): Flow<List<Settings>>
}