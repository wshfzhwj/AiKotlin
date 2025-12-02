package com.example.aikotlin.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.aikotlin.database.NewsDatabase
import com.example.aikotlin.network.ApiClient
import com.example.aikotlin.network.ApiService
import com.example.aikotlin.viewmodel.NewsViewModel

/**
 * 一个通用的 ViewModel Factory，负责创建所有 ViewModel 及其依赖项。
 * 这个工厂现在需要一个 Application Context 来初始化数据库和网络服务。
 */
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val newsRepository by lazy {
        val apiService = ApiClient.apiService
        val newsDatabase = Room.databaseBuilder(
            context.applicationContext,
            NewsDatabase::class.java,
            "news_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        val newsRemoteDataSource = NewsRemoteDataSource(apiService)
        val newsLocalDataSource = NewsLocalDataSource(newsDatabase.newsDao())
        NewsRepository(newsRemoteDataSource, newsLocalDataSource)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // 检查 modelClass 是否是我们知道如何创建的 ViewModel
        if (modelClass.isAssignableFrom(NewsViewModel::class.java)) {
            // 如果是 NewsViewModel，我们就知道如何创建它
            @Suppress("UNCHECKED_CAST")
            return NewsViewModel(newsRepository) as T
        }

        // if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
        //     @Suppress("UNCHECKED_CAST")
        //     return MainViewModel(newsRepository) as T
        // }

        // 如果是未知的 ViewModel 类型，抛出异常
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}