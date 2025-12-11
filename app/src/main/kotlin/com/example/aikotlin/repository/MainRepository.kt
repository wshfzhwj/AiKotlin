package com.example.aikotlin.repository

import android.util.Log
import com.example.aikotlin.base.BaseRepository
import com.example.aikotlin.data.NewsLocalDataSource
import com.example.aikotlin.data.NewsRemoteDataSource
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.model.NewsCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class MainRepository() : BaseRepository() {
}
