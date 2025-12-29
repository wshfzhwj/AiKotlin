package com.example.aikotlin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Video(
    val id: Int,
    val title: String,
    val videoUrl: String,
    val coverUrl: String
) : Parcelable
