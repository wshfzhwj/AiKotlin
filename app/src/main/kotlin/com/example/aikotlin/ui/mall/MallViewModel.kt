package com.example.aikotlin.ui.mall

import androidx.lifecycle.viewModelScope
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.data.MallDataProvider
import com.example.aikotlin.model.MallHomeData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MallViewModel : BaseViewModel() {

    private val _uiState = MutableStateFlow(MallUiState())
    val uiState: StateFlow<MallUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private val pageSize = 10

    init {
        refresh()
    }

    fun refresh() {
        if (_uiState.value.isLoading || _uiState.value.isRefreshing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            try {
                // Simulate network delay
                delay(1000)
                currentPage = 1
                val data = MallDataProvider.getMallHomeData(page = currentPage, pageSize = pageSize)
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        isLoading = false,
                        data = data,
                        hasMoreData = true // Reset for new list
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        isLoading = false,
                        errorMessage = e.message ?: "发生未知错误"
                    )
                }
            }
        }
    }

    fun loadMore() {
        if (_uiState.value.isLoading || _uiState.value.isRefreshing || _uiState.value.isLoadingMore || !_uiState.value.hasMoreData) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true, errorMessage = null) }
            try {
                // Simulate network delay
                delay(1000)
                val nextPage = currentPage + 1
                val newData = MallDataProvider.getMallHomeData(page = nextPage, pageSize = pageSize)
                
                if (newData.feedProducts.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            hasMoreData = false
                        )
                    }
                } else {
                    currentPage = nextPage
                    _uiState.update { currentState ->
                        val currentData = currentState.data ?: MallHomeData(emptyList(), emptyList(), emptyList(), emptyList())
                        val updatedFeed = currentData.feedProducts + newData.feedProducts
                        val updatedData = currentData.copy(feedProducts = updatedFeed)
                        
                        currentState.copy(
                            isLoadingMore = false,
                            data = updatedData,
                            hasMoreData = true
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingMore = false,
                        errorMessage = e.message ?: "发生未知错误"
                    )
                }
            }
        }
    }
}
