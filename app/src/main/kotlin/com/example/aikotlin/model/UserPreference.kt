package com.example.aikotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreference(
    @PrimaryKey
    val keyword: String,
    val value: String
)