package com.example.aikotlin.ui.mall

import androidx.lifecycle.viewModelScope
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.data.MallDataProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MallViewModel : BaseViewModel() {

    private val _uiState = MutableStateFlow<MallUiState>(MallUiState.Loading)
    val uiState: StateFlow<MallUiState> = _uiState.asStateFlow()

    init {
        fetchMallHomeData()
    }

    fun fetchMallHomeData() {
        viewModelScope.launch {
            _uiState.value = MallUiState.Loading
            try {
                // Simulate network delay
                delay(1000)
                val data = MallDataProvider.getMallHomeData()
                _uiState.value = MallUiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = MallUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}
