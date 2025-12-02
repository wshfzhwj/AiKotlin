package com.example.aikotlin.viewmodel

import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.data.NewsRepository
import com.example.aikotlin.model.NewsArticle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

class MainViewModel(
    private val repository: NewsRepository
) : BaseViewModel() {
    
    private val _newsArticles = MutableStateFlow<List<NewsArticle>>(emptyList())
    val newsArticles: StateFlow<List<NewsArticle>> = _newsArticles
    
    private val _currentCategory = MutableStateFlow("general")
    val currentCategory: StateFlow<String> = _currentCategory
    
    // 预定义的新闻分类
    val newsCategories = repository.newsCategories
    
    init {
        // 初始化时加载默认分类的新闻
        loadNewsByCategory(_currentCategory.value)
    }
    
    /**
     * 加载特定分类的新闻
     */
    fun loadNewsByCategory(category: String) {
        _currentCategory.value = category
        // 使用 execute 替代 safeLaunch
        execute {
            // 将业务逻辑直接放在 execute 块内
            repository.getNewsByCategory(category)
                .catch { e ->
                    // 在 Flow 链中处理特定的错误信息
                    mutableErrorMessage.value = "加载 '${category}' 分类失败: ${e.message}"
                }
                .collectLatest { articles -> // 使用 collectLatest 更安全
                    _newsArticles.value = articles
                }
        }
    }
    
    /**
     * 刷新当前分类的新闻
     */
    fun refreshNews() {
        execute {
            // refreshNewsByCategory 应该是一个 suspend 函数
            val refreshedArticles = repository.refreshNewsByCategory(_currentCategory.value)
            _newsArticles.value = refreshedArticles
            // 注意: execute 的 catch 块会自动捕获这里的任何异常
        }
    }
    
    /**
     * 搜索新闻
     */
    fun searchNews(query: String) {
        if (query.isBlank()) {
            // 如果搜索查询为空，可以考虑恢复到默认分类
            loadNewsByCategory(_currentCategory.value)
            return
        }

        // 使用 execute 替代 safeLaunch
        execute {
            val searchResults = repository.searchNews(query)
            _newsArticles.value = searchResults
        }
    }
}