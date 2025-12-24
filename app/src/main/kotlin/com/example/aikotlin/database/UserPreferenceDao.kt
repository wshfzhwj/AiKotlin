package com.example.aikotlin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.model.UserPreference
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreference(preference: UserPreference)

    @Query("SELECT value FROM user_preferences WHERE keyword = :prefKey")
    fun getPreferenceValue(prefKey: String): Flow<List<String>>

    // 修复：删除 Int 返回类型，使其默认返回 Unit (即 void)，以满足 Room 编译器的要求
    @Query("DELETE FROM user_preferences WHERE keyword = :prefKey")
    suspend fun deletePreference(prefKey: String): Int
}