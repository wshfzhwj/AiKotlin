package com.example.aikotlin.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.repository.NewsRepository
import com.example.aikotlin.repository.ResultState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModel(private val repository: NewsRepository) : BaseViewModel() {
    // 创建一个私有的、可变的SharedFlow用于发送一次性事件
    private val _uiEvents = MutableSharedFlow<UiEvent>()

    // 暴露一个不可变的、公开的SharedFlow给UI层订阅
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    private val pageSize = 10
    private val requestFlow = MutableStateFlow(NewsRequest("general", "", 1, true))

    val uiState: StateFlow<NewsUiState> =
        requestFlow.flatMapLatest { request ->
            repository.getNewsByCategoryBySuper(request.category, request.page, pageSize)
        }.map { result -> // 将ResultState映射为UI状态
                val currentState = uiState.value // 获取旧状态
                when (result) {
                    is ResultState.Loading -> currentState.copy(isLoading = requestFlow.value.isRefresh)
                    is ResultState.Success -> {
                        val newArticles = result.data
                        val articles = if (requestFlow.value.isRefresh) {
                            newArticles
                        } else {
                            val currentIds = currentState.articles.map { it.url }.toSet()
                            currentState.articles + newArticles.filter { !currentIds.contains(it.url) }
                        }
                        currentState.copy(
                            isLoading = false,
                            articles = articles,
                            hasMoreData = newArticles.size >= pageSize
                        )
                    }

                    is ResultState.Error -> {
                        _uiEvents.emit(UiEvent.ShowToast(result.message))
                        currentState.copy(isLoading = false)
                    }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // UI在后台5秒后停止收集
                initialValue = NewsUiState() // 初始UI状态
            )


    fun setCategory(category: String) {
        // 如果分类没变，则不执行任何操作
        if (requestFlow.value.category == category) return
        requestFlow.value = requestFlow.value.copy(category = category, page = 1, isRefresh = true)
    }

    fun refresh() {
        // 刷新就是用当前的分类，重置页码为1
        requestFlow.value = requestFlow.value.copy(page = 1, isRefresh = true)
    }


    fun loadMore() {
        if (uiState.value.isLoading || !uiState.value.hasMoreData) return
        // 加载更多就是页码+1，并且不是刷新
        requestFlow.value =
            requestFlow.value.copy(page = requestFlow.value.page + 1, isRefresh = false, keyWords = "")
    }

    fun searchNews(query: String) {
        requestFlow.value = requestFlow.value.copy(keyWords = query)
    }
}

// 可以放在 NewsViewModel文件的顶部
private data class NewsRequest(
    val category: String,
    val keyWords: String,
    val page: Int,
    val isRefresh: Boolean
)

// 定义一个UI状态数据类
data class NewsUiState(
    val isLoading: Boolean = false,
    val articles: List<NewsArticle> = emptyList(),
    val errorMessage: String? = null,
    val hasMoreData: Boolean = true
)
// 定义UI事件的密封类
sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}