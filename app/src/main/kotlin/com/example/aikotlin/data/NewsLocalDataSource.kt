package com.example.aikotlin.data

import android.util.Log
import com.example.aikotlin.database.NewsDao
import com.example.aikotlin.model.NewsArticle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class NewsLocalDataSource (
    private val newsDao: NewsDao
) {
    
     suspend fun getArticlesByCategoryByFlow(category: String): Flow<List<NewsArticle>> {
        Log.e("NewsViewModel", "category = $category")
        val articles = newsDao.getArticlesByCategory(category)
         Log.e("NewsViewModel", "articles = ${articles.first()}")
        return articles
    }

    fun getArticlesByCategoryByList(category: String): List<NewsArticle> {
        return newsDao.getArticlesByCategoryByList(category)
    }
    
    fun getRecentArticles(limit: Int = 20): Flow<List<NewsArticle>> {
        return newsDao.getRecentArticles(limit)
    }
    
    fun getAllCategories(): Flow<List<String>> {
        return newsDao.getAllCategories()
    }
    
    suspend fun saveArticles(articles: List<NewsArticle>) {
        newsDao.insertArticles(articles)
        Log.e("NewsViewModel", "saveArticles")
    }
    
    suspend fun clearAllArticles() {
        newsDao.clearArticles()
    }
}