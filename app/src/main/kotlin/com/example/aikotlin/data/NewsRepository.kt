package com.example.aikotlin.data

import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.model.NewsCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList

class NewsRepository (
    private val remoteDataSource: NewsRemoteDataSource,
    private val localDataSource: NewsLocalDataSource
) {
    // 预定义的新闻分类
    val newsCategories = listOf(
        NewsCategory("general", "综合", 0),
        NewsCategory("business", "财经", 0),
        NewsCategory("technology", "科技", 0),
        NewsCategory("entertainment", "娱乐", 0),
        NewsCategory("sports", "体育", 0),
        NewsCategory("health", "健康", 0),
        NewsCategory("science", "科学", 0)
    )
    
    // 获取特定分类的新闻，返回Flow（优化版）
     fun getNewsByCategory(category: String): Flow<List<NewsArticle>> {
        return flow {
            // 先尝试从本地获取
            val localArticles = localDataSource.getArticlesByCategory(category).first()
            
            if (localArticles.isEmpty()) {
                // 本地没有数据，从网络获取
                try {
                    val remoteArticles = remoteDataSource.getTopHeadlines(category)
                    // 添加分类信息并保存到本地
                    val articlesWithCategory = remoteArticles.map { it.copy(category = category) }
                    localDataSource.saveArticles(articlesWithCategory)
                    emit(articlesWithCategory)
                } catch (e: Exception) {
                    // 网络请求失败，返回空列表
                    emit(emptyList())
                }
            } else {
                // 本地有数据，直接返回
                emit(localArticles)
            }
        }.flowOn(Dispatchers.IO)
    }
    
    // 新版本：直接使用Flow数据源获取新闻
    fun getNewsByCategoryAsFlow(category: String): Flow<List<NewsArticle>> {
        return flow {
            // 先尝试从本地获取
            val localArticles = localDataSource.getArticlesByCategory(category).first()
            if (localArticles.isEmpty()) {
                // 本地没有数据，使用Flow从网络获取
                remoteDataSource.getTopHeadlinesAsFlow(category)
                    .collect { remoteArticles ->
                        val articlesWithCategory = remoteArticles.map { it.copy(category = category) }
                        localDataSource.saveArticles(articlesWithCategory)
                        emit(articlesWithCategory)
                    }
            } else {
                // 本地有数据，直接返回
                emit(localArticles)
            }
        }.flowOn(Dispatchers.IO)
        .catch { e ->
            // 错误处理
            emit(emptyList())
        }
    }
    
    // 刷新特定分类的新闻，返回Flow
    fun refreshNewsByCategoryAsFlow(category: String): Flow<List<NewsArticle>> {
        return flow {
            remoteDataSource.getTopHeadlinesAsFlow(category)
                .collect { remoteArticles ->
                    val articlesWithCategory = remoteArticles.map { it.copy(category = category) }
                    localDataSource.saveArticles(articlesWithCategory)
                    emit(articlesWithCategory)
                }
        }.flowOn(Dispatchers.IO)
        .catch { e ->
            // 错误处理，返回空列表
            emit(emptyList())
        }
    }
    
    // 保留原有刷新方法以保持兼容性
    suspend fun refreshNewsByCategory(category: String): List<NewsArticle> {
        return try {
            val remoteArticles = remoteDataSource.getTopHeadlines(category)
            val articlesWithCategory = remoteArticles.map { it.copy(category = category) }
            localDataSource.saveArticles(articlesWithCategory)
            return articlesWithCategory
        } catch (e: Exception) {
            throw e
        }
    }
    
    // 搜索新闻，返回Flow
    fun searchNewsAsFlow(query: String): Flow<List<NewsArticle>> {
        return remoteDataSource.searchNewsAsFlow(query)
            .flowOn(Dispatchers.IO)
            .catch { e ->
                // 错误处理，返回空列表
                emit(emptyList())
            }
    }
    
    // 保留原有搜索方法以保持兼容性
    suspend fun searchNews(query: String): List<NewsArticle> {
        return try {
            remoteDataSource.searchNews(query)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // 获取最近的新闻（保留并优化）
    fun getRecentNews(limit: Int = 20): Flow<List<NewsArticle>> {
        return localDataSource.getRecentArticles(limit)
            .map { articles ->
                // 确保数据完整
                articles.ifEmpty { emptyList() }
            }
    }
    
    // 获取所有分类的Flow
    fun getAllCategoriesAsFlow(): Flow<List<String>> {
        return localDataSource.getAllCategories()
    }
}