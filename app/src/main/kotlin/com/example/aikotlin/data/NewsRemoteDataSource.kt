package com.example.aikotlin.data

import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class NewsRemoteDataSource (
    private val apiService: ApiService
) {

    // 获取头条新闻，返回Flow类型
    fun getTopHeadlinesAsFlow(category: String?, page : Int = 1, pageSize: Int = 20): Flow<List<NewsArticle>> {
        return flow {
            val apiKey = "your_api_key_here"
            val response = apiService.getTopHeadlines(
                country = "us",
                category = category,
                page = page,
                pageSize = pageSize,
                apiKey = apiKey
            )
            emit(response.articles)
        }.flowOn(Dispatchers.IO) // 在IO线程执行
    }

    // 搜索新闻，返回Flow类型
    fun searchNewsAsFlow(query: String, page: Int, pageSize: Int): Flow<List<NewsArticle>> {
        return flow {
            val apiKey = "your_api_key_here"
            val response = apiService.searchNews(
                query = query,
                page = page,
                pageSize = pageSize,
                apiKey = apiKey
            )
            emit(response.articles)
        }.flowOn(Dispatchers.IO)
    }

    // 保留原有方法以保持兼容性
    suspend fun getTopHeadlines(category: String?, page: Int, pageSize: Int): List<NewsArticle> {
        return getTopHeadlinesAsFlow(category, page, pageSize).first()
    }

    suspend fun searchNews(query: String, page: Int, pageSize: Int): List<NewsArticle> {
        return searchNewsAsFlow(query, page, pageSize).first()
    }
}
