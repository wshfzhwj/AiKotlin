package com.example.aikotlin.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.repository.NewsRepository
import com.example.aikotlin.repository.ResultState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NewsViewModel(private val repository: NewsRepository) : BaseViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    protected val mutableErrorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = mutableErrorMessage
    private val _newsArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val newsArticles: StateFlow<List<NewsArticle>> = _newsArticles

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> = _hasMoreData

    private var currentPage = 1
    private val pageSize = 10
    private var selectedCategory = "general"
    private var newsJob: Job? = null

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
        newsJob?.cancel() // Cancel any ongoing job before starting a new one
        loadNews(isRefresh = true)
    }

    fun loadMore() {
        if (isLoading.value || !hasMoreData.value) return
        currentPage++
        loadNews(isRefresh = false)
    }


    private fun loadNews(isRefresh: Boolean){
//        loadNewsByFlow(isRefresh)
        loadNewsBySuper(isRefresh)
//        loadNewsByChannel(isRefresh)
    }


    private fun loadNewsBySuper(isRefresh: Boolean) {
        newsJob = viewModelScope.launch {
            repository.getNewsByCategoryBySuper(selectedCategory, currentPage, pageSize)
                .collect { result->
                    when(result){
                        is ResultState.Loading -> {
                            if (isRefresh) { // Only show full-screen loading on refresh
                                _isLoading.value = true
                            }
                        }
                        is ResultState.Success -> {
                            Log.e("NewsViewModel", "selectedCategory $selectedCategory")
                            Log.e("NewsViewModel", "result.data ${result.data}")
                            _isLoading.value = false
                            val newArticles = result.data
                            _hasMoreData.value = newArticles.size >= pageSize
                            if (isRefresh) {
                                _newsArticles.value = newArticles
                            } else {
                                // Prevent duplicates by ensuring the new articles are not already in the list
                                val currentIds = _newsArticles.value.map { it.url }.toSet()
                                val uniqueNewArticles = newArticles.filter { !currentIds.contains(it.url) }
                                _newsArticles.value += uniqueNewArticles
                            }
                        }
                        is ResultState.Error -> {
                            _isLoading.value = false
                            mutableErrorMessage.value = result.message
                        }
                    }
                }
        }
    }

    private fun loadNewsByFlow(isRefresh: Boolean) {
        viewModelScope.launch {
            repository.getNewsByCategoryByFlow(selectedCategory, currentPage, pageSize)
                .collect { result->
                    when(result){
                     is ResultState.Loading -> {
                         _isLoading.value = true
                     }
                     is ResultState.Success -> {
                         Log.e("NewsViewModel", "loadNews Success: ${result.data}")
                         _isLoading.value = false
                         val newArticles = result.data
                         _hasMoreData.value = newArticles.size >= pageSize
                         if (isRefresh) {
                             _newsArticles.value = newArticles
                         } else {
                             _newsArticles.value += newArticles
                         }
                     }
                        is ResultState.Error -> {
                            _isLoading.value = false
                            mutableErrorMessage.value = result.message
                        }
                    }
                }
        }
    }

    private fun loadNewsByChannel(isRefresh: Boolean) {
        viewModelScope.launch {
            repository.getNewsByCategoryByChannelFlow(selectedCategory, currentPage, pageSize)
                .collect { result->
                    when(result){
                        is ResultState.Loading -> {
                            _isLoading.value = true
                        }
                        is ResultState.Success -> {
                            Log.e("NewsViewModel", "loadNews Success: ${result.data}")
                            _isLoading.value = false
                            val newArticles = result.data
                            _hasMoreData.value = newArticles.size >= pageSize
                            if (isRefresh) {
                                _newsArticles.value = newArticles
                            } else {
                                _newsArticles.value += newArticles
                            }
                        }
                        is ResultState.Error -> {
                            _isLoading.value = false
                            mutableErrorMessage.value = result.message
                        }
                    }
                }
        }
    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            repository.searchNewsAsFlow(query).collect { searchedArticles ->
                _newsArticles.value = searchedArticles
                _hasMoreData.value = false // Search results are not paginated
            }
        }
    }

    fun clearError() {
        mutableErrorMessage.value = null
    }
}
