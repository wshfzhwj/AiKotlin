package com.example.aikotlin.data

import com.example.aikotlin.database.NewsDao
import com.example.aikotlin.model.NewsArticle
import kotlinx.coroutines.flow.Flow

class NewsLocalDataSource(
    private val newsDao: NewsDao
) {

    fun getArticlesByCategoryByFlow(category: String): Flow<List<NewsArticle>> {
        return newsDao.getArticlesByCategory(category)
    }

    fun getArticlesByCategoryByList(category: String): List<NewsArticle> {
        return newsDao.getArticlesByCategoryAsList(category)
    }

    fun getRecentArticles(limit: Int = 20): Flow<List<NewsArticle>> {
        return newsDao.getRecentArticles(limit)
    }

    fun getAllCategories(): Flow<List<String>> {
        return newsDao.getAllCategories()
    }

    fun saveArticles(articles: List<NewsArticle>) {
        newsDao.insertArticles(articles)
    }

    fun clearAllArticles() {
        newsDao.clearArticles()
    }
}