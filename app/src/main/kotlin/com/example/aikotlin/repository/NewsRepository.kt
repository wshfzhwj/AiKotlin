package com.example.aikotlin.repository

import android.util.Log
import com.example.aikotlin.base.BaseRepository
import com.example.aikotlin.data.NewsLocalDataSource
import com.example.aikotlin.data.NewsRemoteDataSource
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.model.NewsCategory
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
    val newsCategories = listOf(
        NewsCategory("general", "综合", 0),
        NewsCategory("business", "财经", 0),
        NewsCategory("technology", "科技", 0),
        NewsCategory("entertainment", "娱乐", 0),
        NewsCategory("sports", "体育", 0),
        NewsCategory("health", "健康", 0),
        NewsCategory("science", "科学", 0)
    )

    fun getNewsByCategoryByFlow(category: String, page: Int = 1, pageSize: Int = 20) = flow {
        emit(ResultState.Loading)
        val cache = localDataSource.getArticlesByCategoryByList(category)
        emit(ResultState.Success(cache))
//        if (!cache.isEmpty()) {
//            emit(ResultState.Success(cache))
//        }
        try {
            val remote = remoteDataSource.getTopHeadlinesAsFlow(category, page, pageSize).first().map { it.copy(category = category) }
            localDataSource.saveArticles(remote)
            emit(ResultState.Success(remote))
        } catch (e: Exception) {
            emit(ResultState.Error(e, resolveError(e)))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getNewsByCategoryByChannelFlow(category: String, page: Int = 1, pageSize: Int = 20) =
        channelFlow {
            send(ResultState.Loading)
            send(ResultState.Success(localDataSource.getArticlesByCategoryByList(category)))
            try {
                remoteDataSource.getTopHeadlinesAsFlow(category, page, pageSize)
                    .collect { remoteArticles ->
                        val articlesWithCategory =
                            remoteArticles.map { it.copy(category = category) }
                        localDataSource.saveArticles(articlesWithCategory)
                        send(ResultState.Success(articlesWithCategory))
                    }
            } catch (e: Exception) {
                send(ResultState.Error(e, resolveError(e)))
                Log.e("NewsRepository", "Failed to fetch from network for category: $category", e)
            }
        }.flowOn(Dispatchers.IO)


    suspend fun getNewsByCategoryBySuper(
        category: String,
        page: Int = 1,
        pageSize: Int = 20
    ): Flow<ResultState<List<NewsArticle>>> {
        Log.e("NewsViewModel", "NewsRepository getNewsByCategoryBySuper")
//        return networkBoundResourceFlowGPT(
//            queryFromDb = { localDataSource.getArticlesByCategoryByFlow(category) },
//            shouldFetch = { cachedArticles -> cachedArticles == null || cachedArticles.isEmpty() },
//            fetchNetwork = { remoteDataSource.getTopHeadlines(category, page, pageSize) },
//            saveCallResult = { articles ->
//                val articlesWithCategory = articles.map { it.copy(category = category) }
//                localDataSource.saveArticles(articlesWithCategory)
//            },
//        ).flowOn(Dispatchers.IO)

//        return networkBoundResource(
//            queryFromDb = { localDataSource.getArticlesByCategoryByFlow(category) },
////            shouldFetch = { cachedArticles -> cachedArticles == null || cachedArticles.isEmpty() },
//            fetchNetwork = { remoteDataSource.getTopHeadlines(category, page, pageSize) },
//            saveCallResult = { articles ->
//                val articlesWithCategory = articles.map { it.copy(category = category) }
//                localDataSource.saveArticles(articlesWithCategory)
//            }
//        ).flowOn(Dispatchers.IO)

        return fetchDataIncremental(
            queryFromDb = { localDataSource.getArticlesByCategoryByFlow(category) },
            shouldFetch = { cachedArticles -> cachedArticles == null || cachedArticles.isEmpty() },
            fetchNetwork = { remoteDataSource.getTopHeadlines(category, page, pageSize) },
            saveCallResult = { articles ->
                val articlesWithCategory = articles.map { it.copy(category = category) }
                localDataSource.saveArticles(articlesWithCategory)
            }
        ).flowOn(Dispatchers.IO)

//        return networkBoundResourceFlowEmitAll(
//            queryFromDb = { localDataSource.getArticlesByCategoryByFlow(category) },
//            shouldFetch = { cachedArticles -> cachedArticles == null || cachedArticles.isEmpty() },
//            fetchNetwork = { remoteDataSource.getTopHeadlines(category, page, pageSize) },
//            saveCallResult = { articles ->
//                val articlesWithCategory = articles.map { it.copy(category = category) }
//                localDataSource.saveArticles(articlesWithCategory)
//            },
//            typeMap = {it}
//        ).flowOn(Dispatchers.IO)

//        return networkBoundResourceFlow(
//            queryFromDb = { localDataSource.getArticlesByCategoryByFlow(category) },
//            shouldFetch = { cachedArticles -> cachedArticles.isEmpty()},
//            fetchNetwork = { remoteDataSource.getTopHeadlines(category, page, pageSize) },
//            saveCallResult = { articles ->
//                val articlesWithCategory = articles.map { it.copy(category = category) }
//                localDataSource.saveArticles(articlesWithCategory)
//            },
//            typeMap = {it}
//        ).flowOn(Dispatchers.IO)
    }


    suspend fun getNewsByCategoryByList(
        category: String,
        page: Int = 1,
        pageSize: Int = 20
    ): List<NewsArticle> {
        // 先尝试从本地获取
        val localArticles = localDataSource.getArticlesByCategoryByList(category)
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
    fun getNewsByCategory(category: String): Flow<List<NewsArticle>> {
        return flow {
            // 先尝试从本地获取
            val localArticles = localDataSource.getArticlesByCategoryByList(category)
            if (localArticles.isEmpty()) {
                // 本地没有数据，使用Flow从网络获取
                remoteDataSource.getTopHeadlinesAsFlow(category)
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

    fun getRecentNews(limit: Int = 20): Flow<List<NewsArticle>> {
        return localDataSource.getRecentArticles(limit)
            .map { articles -> articles.ifEmpty { emptyList() } }
    }

    fun getAllCategoriesAsFlow(): Flow<List<String>> {
        return localDataSource.getAllCategories()
    }

}
