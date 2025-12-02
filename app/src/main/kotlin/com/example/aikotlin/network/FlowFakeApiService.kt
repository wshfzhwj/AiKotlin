package com.example.aikotlin.network

import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.model.NewsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * 基于Flow的模拟API服务，扩展自FakeApiService
 */
class FlowFakeApiService : FakeApiService() {
    
    // 使用Flow获取头条新闻
    fun getTopHeadlinesAsFlow(
        country: String,
        category: String?,
        page: Int,
        pageSize: Int,
        apiKey: String
    ): Flow<NewsResponse> {
        return flow {
            // 模拟网络延迟
            val response = super.getTopHeadlines(country, category, page, pageSize, apiKey)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
    
    // 使用Flow搜索新闻
    fun searchNewsAsFlow(
        query: String,
        page: Int,
        pageSize: Int,
        apiKey: String
    ): Flow<NewsResponse> {
        return flow {
            // 模拟网络延迟
            val response = super.searchNews(query, page, pageSize, apiKey)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
    
    // 直接返回Flow<List<NewsArticle>>的便捷方法
    fun getTopHeadlinesArticlesAsFlow(
        country: String,
        category: String?,
        page: Int = 1,
        pageSize: Int = 20,
        apiKey: String = "fake_key"
    ): Flow<List<NewsArticle>> {
        return flow {
            val response = super.getTopHeadlines(country, category, page, pageSize, apiKey)
            emit(response.articles)
        }.flowOn(Dispatchers.IO)
    }
    
    // 搜索新闻并直接返回Flow<List<NewsArticle>>
    fun searchNewsArticlesAsFlow(
        query: String,
        page: Int = 1,
        pageSize: Int = 20,
        apiKey: String = "fake_key"
    ): Flow<List<NewsArticle>> {
        return flow {
            val response = super.searchNews(query, page, pageSize, apiKey)
            emit(response.articles)
        }.flowOn(Dispatchers.IO)
    }
}