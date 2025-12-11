package com.example.aikotlin.network

import com.example.aikotlin.data.fake.FakeNewsDataStore
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.model.NewsResponse
import kotlinx.coroutines.delay
import kotlin.math.min

/**
 * 使用内存数据模拟真实网络请求。
 * 模拟对返回数据进行加密。
 */
open class FakeApiService : ApiService {
    
    override suspend fun getTopHeadlines(
        country: String,
        category: String?,
        page: Int,
        pageSize: Int,
        apiKey: String
    ): NewsResponse {
        delay(800)
        val articles = FakeNewsDataStore.getArticlesByCategory(category)
        return buildPagedResponse(articles, page, pageSize)
    }
    
    override suspend fun searchNews(
        query: String,
        page: Int,
        pageSize: Int,
        apiKey: String
    ): NewsResponse {
        delay(600)
        val articles = FakeNewsDataStore.searchArticles(query)
        // 模拟网络层返回加密数据
        return buildPagedResponse(articles, page, pageSize)
    }
    
    private fun buildPagedResponse(
        allArticles: List<NewsArticle>,
        page: Int,
        pageSize: Int
    ): NewsResponse {
        val safePage = page.coerceAtLeast(1)
        val fromIndex = (safePage - 1) * pageSize
        if (fromIndex >= allArticles.size) {
            return NewsResponse(
                status = "ok",
                totalResults = allArticles.size,
                articles = emptyList()
            )
        }
        val toIndex = min(fromIndex + pageSize, allArticles.size)
        val pagedArticles = allArticles.subList(fromIndex, toIndex)
        return NewsResponse(
            status = "ok",
            totalResults = allArticles.size,
            articles = pagedArticles
        )
    }
}
