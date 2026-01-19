package com.example.aikotlin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aikotlin.model.NewsArticle
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertArticles(articles: List<NewsArticle>): List<Long>
    
    @Query("DELETE FROM news_articles")
    fun clearArticles(): Int
    
    @Query("SELECT * FROM news_articles WHERE category = :category LIMIT :pageSize OFFSET (:page - 1) * :pageSize")
    fun getArticlesByCategory(category: String, page: Int = 1, pageSize: Int): Flow<List<NewsArticle>>

    @Query("SELECT * FROM news_articles WHERE category = :category LIMIT :pageSize OFFSET (:page - 1) * :pageSize")
    fun getArticlesByCategoryAsList(category: String, page: Int = 1, pageSize: Int = 20): List<NewsArticle>

    @Query("SELECT * FROM news_articles LIMIT :limit")
    fun getRecentArticles(limit: Int = 20): Flow<List<NewsArticle>>
    
    @Query("SELECT DISTINCT category FROM news_articles")
    fun getAllCategories(): Flow<List<String>>
}