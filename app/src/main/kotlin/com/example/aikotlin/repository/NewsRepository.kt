package com.example.aikotlin.repository

import android.util.Log
import com.example.aikotlin.base.BaseRepository
import com.example.aikotlin.data.NewsLocalDataSource
import com.example.aikotlin.data.NewsRemoteDataSource
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NewsRepository(
    private val remoteDataSource: NewsRemoteDataSource,
    private val localDataSource: NewsLocalDataSource
) : BaseRepository() {
    suspend fun getNewsByCategoryByFlow(category: String, page: Int = 1, pageSize: Int = 20) =
        flow {
            val cache = localDataSource.getArticlesByCategoryByList(category, page, pageSize)
            emit(Result.success(cache))
//        if (!cache.isEmpty()) {
//            emit(Result.success(cache))
//        }
            try {
                val remote =
                    remoteDataSource.getTopHeadlinesAsFlow(category, page, pageSize).first()
                        .map { it.copy(category = category) }
                localDataSource.saveArticles(remote)
                emit(Result.success(remote))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)

    suspend fun getNewsByCategoryByChannelFlow(category: String, page: Int, pageSize: Int) =
        channelFlow {
            send(
                Result.success(
                    localDataSource.getArticlesByCategoryByList(
                        category,
                        page,
                        pageSize
                    )
                )
            )
            try {
                remoteDataSource.getTopHeadlinesAsFlow(category, page, pageSize)
                    .collect { remoteArticles ->
                        val articlesWithCategory =
                            remoteArticles.map { it.copy(category = category) }
                        localDataSource.saveArticles(articlesWithCategory)
                        send(Result.success(articlesWithCategory))
                    }
            } catch (e: Exception) {
                send(Result.failure(e))
            }
        }.flowOn(Dispatchers.IO)


    suspend fun queryDBByCategory(
        category: String,
        page: Int,
        pageSize: Int
    ): Flow<List<NewsArticle>> {
        return localDataSource.getArticlesByCategoryByFlow(category, page, pageSize)
    }

    suspend fun getNewsByCategoryBySuper(
        category: String,
        query: String,
        page: Int,
        pageSize: Int
    ): Flow<Result<List<NewsArticle>>> {
        return networkBoundResourceFlowEmitAll(
            queryFromDb = { queryDBByCategory(category, page, pageSize) },
//            shouldFetch = { cachedArticles -> cachedArticles == null || cachedArticles.isEmpty() },
            fetchNetwork = { remoteDataSource.getTopHeadlines(category, page, pageSize) },
            saveCallResult = { articles ->
                val articlesWithCategory = articles.map { it.copy(category = category) }
                localDataSource.saveArticles(articlesWithCategory)
            },
            typeMap = { it }
        ).flowOn(Dispatchers.IO)
    }


    suspend fun getNewsByCategoryByList(
        category: String,
        page: Int,
        pageSize: Int
    ): List<NewsArticle> {
        // 先尝试从本地获取
        val localArticles = localDataSource.getArticlesByCategoryByList(category, page, pageSize)
        if (localArticles.isEmpty()) {
            // 本地没有数据，使用Flow从网络获取
            val remoteArticles =
                remoteDataSource.getTopHeadlines(category, page, pageSize)
            localDataSource.saveArticles(remoteArticles)
            return remoteArticles
        } else {
            // 本地有数据，直接返回
            return localArticles
        }
    }

    //    // 新版本：直接使用Flow数据源获取新闻
    fun getNewsByCategory(category: String, page: Int, pageSize: Int): Flow<List<NewsArticle>> {
        return flow {
            // 先尝试从本地获取
            val localArticles =
                localDataSource.getArticlesByCategoryByList(category, page, pageSize)
            if (localArticles.isEmpty()) {
                // 本地没有数据，使用Flow从网络获取
                remoteDataSource.getTopHeadlinesAsFlow(category, page, pageSize)
                    .collect { remoteArticles ->
                        val articlesWithCategory =
                            remoteArticles.map { it.copy(category = category) }
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

    /**
     * A new suspend function that fetches news directly from the network.
     * This is what BaseViewModel's execute method expects.
     */
    suspend fun getNewsFromNetwork(category: String, page: Int, pageSize: Int): List<NewsArticle> {
        val remoteArticles = remoteDataSource.getTopHeadlines(category, page, pageSize)
        return remoteArticles
    }

    fun searchNewsAsFlow(query: String): Flow<List<NewsArticle>> {
        return remoteDataSource.searchNewsAsFlow(query, 1, 20)
            .flowOn(Dispatchers.IO)
            .catch { e -> emit(emptyList()) }
    }

    fun getRecentNews(limit: Int): Flow<List<NewsArticle>> {
        return localDataSource.getRecentArticles(limit)
            .map { articles -> articles.ifEmpty { emptyList() } }
    }

    fun getAllCategoriesAsFlow(): Flow<List<String>> {
        return localDataSource.getAllCategories()
    }

}
