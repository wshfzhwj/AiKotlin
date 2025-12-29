package com.example.aikotlin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoItem(
    val id: String,
    val title: String,
    val coverUrl: String,
    val playCount: String,
    val duration: String,
    val authorName: String,
    val authorAvatar: String
) : Parcelable
