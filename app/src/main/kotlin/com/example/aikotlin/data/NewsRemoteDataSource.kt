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
    fun getTopHeadlinesAsFlow(category: String? = null, page: Int = 1): Flow<List<NewsArticle>> {
        return flow {
            // 注意：实际使用时需要从安全存储获取API Key
            val apiKey = "your_api_key_here"
            val response = apiService.getTopHeadlines(
                country = "us",
                category = category,
                page = page,
                pageSize = 20,
                apiKey = apiKey
            )
            emit(response.articles)
        }.flowOn(Dispatchers.IO) // 在IO线程执行
    }
    
    // 搜索新闻，返回Flow类型
    fun searchNewsAsFlow(query: String, page: Int = 1): Flow<List<NewsArticle>> {
        return flow {
            val apiKey = "your_api_key_here"
            val response = apiService.searchNews(
                query = query,
                page = page,
                pageSize = 20,
                apiKey = apiKey
            )
            emit(response.articles)
        }.flowOn(Dispatchers.IO)
    }
    
    // 保留原有方法以保持兼容性
    suspend fun getTopHeadlines(category: String? = null, page: Int = 1): List<NewsArticle> {
        return getTopHeadlinesAsFlow(category, page).first()
    }
    
    suspend fun searchNews(query: String, page: Int = 1): List<NewsArticle> {
        return searchNewsAsFlow(query, page).first()
    }
}