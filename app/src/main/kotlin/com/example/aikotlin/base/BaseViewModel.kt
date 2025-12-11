package com.example.aikotlin.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aikotlin.repository.NewsRepository
import com.example.aikotlin.repository.ResultState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseViewModel(private val repo: BaseRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    protected val mutableErrorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = mutableErrorMessage

    /**
     * Executes a suspend function (e.g., a network call) by wrapping it with safeApiCall,
     * and automatically handles the UI state (loading, error, success).
     *
     * @param block The suspend function to be executed.
     * @param onResult An optional callback to handle the successful result data.
     */
    protected fun <T> execute(
        block: suspend () -> T,
        onResult: (T) -> Unit = {}
    ) {
        repo.safeApiCall(block)
            .onEach { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _isLoading.value = true
                    }
                    is ResultState.Success -> {
                        _isLoading.value = false
                        onResult(result.data)
                    }
                    is ResultState.Error -> {
                        _isLoading.value = false
                        mutableErrorMessage.value = result.exception.message ?: "An unknown error occurred"
                    }
                }
            }.launchIn(viewModelScope)
    }

    /**
     * Clears the error message.
     */
    fun clearError() {
        mutableErrorMessage.value = null
    }
}
