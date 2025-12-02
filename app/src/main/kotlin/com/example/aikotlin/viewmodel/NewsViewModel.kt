// file: app/src/main/kotlin/com/example/aikotlin/viewmodel/NewsViewModel.kt
package com.example.aikotlin.viewmodel

import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.data.NewsRepository
import com.example.aikotlin.model.NewsArticle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList

class NewsViewModel(private val repository: NewsRepository) : BaseViewModel() {

    private val _newsArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val newsArticles: StateFlow<List<NewsArticle>> = _newsArticles

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData

    private var currentPage = 1
    private val pageSize = 10
//    private var selectedCategory = "general"
    private var selectedCategory = "all"

    init {
        loadNewsArticles()
    }

    fun setCategory(category: String) {
        if (selectedCategory == category) return
        selectedCategory = category
        refresh()
    }

    fun refresh() {
        currentPage = 1
        _hasMoreData.value = true
        loadNewsArticles()
    }

    fun loadMore() {
        // 使用 !isLoading.value 保护，防止并发加载
        if (isLoading.value || !hasMoreData.value) return

        execute {
            val currentArticleList = _newsArticles.value
            val moreArticles = repository.getNewsByCategory(selectedCategory).first()
                .drop(currentArticleList.size) // 从完整列表中跳过已加载的部分
                .take(pageSize)

            if (moreArticles.isNotEmpty()) { // 这里的 isNotEmpty() 现在可以正常工作
                val updatedList = currentArticleList + moreArticles
                _newsArticles.value = updatedList
                currentPage++
            } else {
                _hasMoreData.value = false
            }
        }
    }


    private fun loadNewsArticles() {
        execute {
            repository.getNewsByCategory(selectedCategory)
                .catch { e ->
                    // Flow 内部的 catch 只处理上游异常，但 execute 的 catch 会作为最终保障
                    mutableErrorMessage.value = "Failed to load articles: ${e.message}"
                }
                .collectLatest { articles ->
                    // 处理分页
                    _newsArticles.value = articles.take(pageSize)

                    // 判断是否有更多数据
                    _hasMoreData.value = articles.size > pageSize
                    currentPage = 1
                }
        }
    }

    fun searchNews(query: String) {
        execute {
            repository.searchNewsAsFlow(query)
                .catch { e ->
                    mutableErrorMessage.value = "Search failed: ${e.message}"
                }
                .collectLatest { articles ->
                    _newsArticles.value = articles
                    _hasMoreData.value = false // 搜索结果不分页
                }
        }
    }
}
