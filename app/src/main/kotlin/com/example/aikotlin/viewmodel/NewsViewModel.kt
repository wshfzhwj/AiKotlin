package com.example.aikotlin.viewmodel

import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NewsViewModel(private val repository: NewsRepository) : BaseViewModel(repository) {

    private val _newsArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val newsArticles: StateFlow<List<NewsArticle>> = _newsArticles

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData

    private var currentPage = 1
    private val pageSize = 10
    private var selectedCategory = "general"

    init {
        refresh()
    }

    fun setCategory(category: String) {
        if (selectedCategory == category) return
        selectedCategory = category
        refresh()
    }

    fun refresh() {
        currentPage = 1
        loadNews(isRefresh = true)
    }

    fun loadMore() {
        if (isLoading.value || !hasMoreData.value) return
        currentPage++
        loadNews(isRefresh = false)
    }

    private fun loadNews(isRefresh: Boolean) {
        execute(
            block = { repository.getNewsByCategoryByList(selectedCategory, currentPage, pageSize) },
            onResult = { newArticles ->
                _hasMoreData.value = newArticles.size >= pageSize

                if (isRefresh) {
                    _newsArticles.value = newArticles
                } else {
                    _newsArticles.value += newArticles
                }
            }
        )
    }

    fun searchNews(query: String) {
        execute(
            block = { repository.searchNews(query) },
            onResult = { searchedArticles ->
                _newsArticles.value = searchedArticles
                _hasMoreData.value = false // Search results are not paginated
            }
        )
    }
}
