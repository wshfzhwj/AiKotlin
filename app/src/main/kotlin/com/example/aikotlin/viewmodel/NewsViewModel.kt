package com.example.aikotlin.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.repository.NewsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class NewsViewModel(private val repository: NewsRepository) : BaseViewModel() {

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState
    private val requestFlow = MutableStateFlow(NewsRequest("all", "", 1, 0L))
    private var newsJob: Job? = null
    private var page = 1
    private var pageSize = 5

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

    fun refresh() {
        if (_uiState.value.isRefreshing || _uiState.value.isLoading) {
            return
        }
        requestFlow.value =
            requestFlow.value.copy(page = 1, refreshTrigger = System.currentTimeMillis())
    }

    fun loadMore() {
        if (_uiState.value.isRefreshing || _uiState.value.isLoading) {
            return
        }
        page++
        requestFlow.value =
            requestFlow.value.copy(page = this.page, refreshTrigger = System.currentTimeMillis())
    }

    /**
     * 根据请求参数获取新闻数据。
     */
    private fun fetchNews(request: NewsRequest): Job = viewModelScope.launch {
        if (request.page == 1) {
            _uiState.update { it.copy(isRefreshing = true) }
        }else{
            _uiState.update { it.copy(isLoadingMore = true) }
        }

        // 调用仓库层获取数据流，并添加异常捕获
        repository.getNewsByCategoryBySuper(
            category = request.category,
            query = request.keyWords,
            page = request.page,
            pageSize = pageSize
        ).catch { e ->
            // 捕获流处理中发生的未知异常
            _uiEvents.emit(
                UiEvent.ShowToast(
                    e.localizedMessage ?: "An unexpected error occurred"
                )
            )
            _uiState.update {
                it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    isLoadingMore = false,
                    loadMoreError = it.isLoadingMore
                )
            }
        }.collect { result ->
            // 根据成功或失败的结果来更新UI状态
            result.onSuccess { data ->
                _uiState.update { currentState ->
                    val articles = if (_uiState.value.isRefreshing || _uiState.value.isLoading) {
                        data
                    } else {
                        // 加载更多时，过滤掉已存在的文章，然后追加到列表末尾
                        val currentUrls = currentState.articles.map { it.url }.toSet()
                        currentState.articles + data.filterNot { it.url in currentUrls }
                    }
                    currentState.copy(
                        isLoading = false,
                        isRefreshing = false,
                        isLoadingMore = false,
                        loadMoreError = false,
                        articles = articles,
                        hasMoreData = data.size >= pageSize
                    )
                }
            }
            result.onFailure { e ->
                _uiEvents.emit(UiEvent.ShowToast(e.message!!))
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        isRefreshing = false,
                        loadMoreError = it.isLoadingMore
                    )
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
        if (requestFlow.value.category == category) return
        page = 1
        requestFlow.value = requestFlow.value.copy(
            page = this.page,
            category = category,
            refreshTrigger = System.currentTimeMillis()
        )
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
        page = 1
        requestFlow.value = requestFlow.value.copy(
            page = this.page,
            keyWords = query,
            refreshTrigger = System.currentTimeMillis()
        )
    }
}

// 内部数据类，用于封装所有请求参数
private data class NewsRequest(
    val category: String,
    val keyWords: String,
    val page: Int,
    val refreshTrigger: Long = 0L,
)

// UI状态数据类，用于驱动UI渲染
data class NewsUiState(
    val hasMoreData: Boolean = true,
    val articles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val loadMoreError: Boolean = false
)

// UI一次性事件的密封类
sealed class UiEvent {
    data class ShowToast(val message: String?) : UiEvent()
}