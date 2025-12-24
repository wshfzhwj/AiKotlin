package com.example.aikotlin.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)