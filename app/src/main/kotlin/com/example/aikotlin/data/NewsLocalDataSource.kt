package com.example.aikotlin.data

import com.example.aikotlin.database.NewsDao
import com.example.aikotlin.model.NewsArticle
import kotlinx.coroutines.flow.Flow

class NewsLocalDataSource(
    private val newsDao: NewsDao
) {

    fun getArticlesByCategoryByFlow(category: String, page: Int = 1, pageSize: Int = 20): Flow<List<NewsArticle>> {
        return newsDao.getArticlesByCategory(category, page, pageSize)
    }

    fun getArticlesByCategoryByList(category: String, page: Int = 1, pageSize: Int = 20): List<NewsArticle> {
        return newsDao.getArticlesByCategoryAsList(category, page, pageSize)
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