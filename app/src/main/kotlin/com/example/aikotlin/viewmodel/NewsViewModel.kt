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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModel(private val repository: NewsRepository) : BaseViewModel() {

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState

    private val pageSize = 10
    private val requestFlow = MutableStateFlow(NewsRequest("general", "", 1, true))

    private var newsJob: Job? = null

    init {
        // 监听请求流的变化，每当请求更新时，触发一次新的数据获取
        viewModelScope.launch {
            requestFlow.collect { request ->
                // 取消上一个正在进行的任务，然后开始新的
                newsJob?.cancel()
                newsJob = fetchNews(request)
            }
        }
    }

    /**
     * 根据请求参数获取新闻数据。
     * @param request 包含分类、关键词、页码等信息的新闻请求。
     */
    private fun fetchNews(request: NewsRequest): Job = viewModelScope.launch {
        // 如果是刷新操作，则显示加载动画
        if (request.isRefresh) {
            _uiState.update { it.copy(isLoading = true) }
        }

        // 调用仓库层获取数据流，并添加异常捕获
        repository.getNewsByCategoryBySuper(
            category = request.category,
            query = request.keyWords,
            page = request.page,
            pageSize = pageSize
        ).catch { e ->
            // 捕获流处理中发生的未知异常
            _uiEvents.emit(UiEvent.ShowToast(e.localizedMessage ?: "An unexpected error occurred"))
            _uiState.update { it.copy(isLoading = false) }
        }.collect { result ->
            // 根据成功或失败的结果来更新UI状态
            when (result) {
                is ResultState.Success -> {
                    val newArticles = result.data
                    _uiState.update { currentState ->
                        val articles = if (request.isRefresh) {
                            newArticles
                        } else {
                            // 加载更多时，过滤掉已存在的文章，然后追加到列表末尾
                            val currentUrls = currentState.articles.map { it.url }.toSet()
                            currentState.articles + newArticles.filterNot { it.url in currentUrls }
                        }
                        currentState.copy(
                            isLoading = false,
                            articles = articles,
                            hasMoreData = newArticles.size >= pageSize
                        )
                    }
                }
                is ResultState.Error -> {
                    _uiEvents.emit(UiEvent.ShowToast(result.message))
                    _uiState.update { it.copy(isLoading = false) }
                }
                is ResultState.Loading -> {
                   // 可选：仓库层也返回了Loading状态，这里可以根据需要处理
                }
            }
        }
    }

    /**
     * 设置新闻分类，并触发刷新。
     * @param category 分类ID。
     */
    fun setCategory(category: String) {
        // 如果分类未改变且当前不是在搜索模式，则不执行任何操作
        if (requestFlow.value.category == category && requestFlow.value.keyWords.isEmpty()) return
        requestFlow.value = NewsRequest(category = category, keyWords = "", page = 1, isRefresh = true)
    }

    /**
     * 刷新当前列表。
     */
    fun refresh() {
        requestFlow.value = requestFlow.value.copy(page = 1, isRefresh = true)
    }

    /**
     * 加载更多数据。
     */
    fun loadMore() {
        if (_uiState.value.isLoading || !_uiState.value.hasMoreData) return
        // 在现有请求的基础上，页码+1，并标记为非刷新
        requestFlow.value = requestFlow.value.copy(page = requestFlow.value.page + 1, isRefresh = false)
    }

    /**
     * 根据关键词搜索新闻。
     * @param query 搜索关键词。
     */
    fun searchNews(query: String) {
        // 如果搜索词为空，则恢复到当前分类的列表
        if (query.isBlank()) {
            refresh()
            return
        }
        // 新的搜索总是从第一页开始，并标记为刷新
        requestFlow.value = NewsRequest(category = "", keyWords = query, page = 1, isRefresh = true)
    }
}

// 内部数据类，用于封装所有请求参数
private data class NewsRequest(
    val category: String,
    val keyWords: String,
    val page: Int,
    val isRefresh: Boolean
)

// UI状态数据类，用于驱动UI渲染
data class NewsUiState(
    val isLoading: Boolean = false,
    val articles: List<NewsArticle> = emptyList(),
    val errorMessage: String? = null,
    val hasMoreData: Boolean = true
)

// UI一次性事件的密封类
sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}
