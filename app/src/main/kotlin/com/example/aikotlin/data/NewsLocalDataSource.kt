package com.example.aikotlin.data

import com.example.aikotlin.database.NewsDao
import com.example.aikotlin.model.NewsArticle
import kotlinx.coroutines.flow.Flow

class NewsLocalDataSource (
    private val newsDao: NewsDao
) {
    
    fun getArticlesByCategory(category: String): Flow<List<NewsArticle>> {
        return newsDao.getArticlesByCategory(category)
    }
    
    fun getRecentArticles(limit: Int = 20): Flow<List<NewsArticle>> {
        return newsDao.getRecentArticles(limit)
    }
    
    fun getAllCategories(): Flow<List<String>> {
        return newsDao.getAllCategories()
    }
    
    suspend fun saveArticles(articles: List<NewsArticle>) {
        newsDao.insertArticles(articles)
    }
    
    suspend fun clearAllArticles() {
        newsDao.clearArticles()
    }
}