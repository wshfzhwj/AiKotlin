// file: app/src/main/kotlin/com/example/aikotlin/base/BaseViewModel.kt
package com.example.aikotlin.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    protected val mutableErrorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = mutableErrorMessage

    /**
     * 执行一个协程任务，并自动管理加载状态和异常。
     * @param block 将要执行的挂起函数。
     */
    protected fun execute(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            mutableErrorMessage.value = null // 开始新任务前清除旧错误
            try {
                block()
            } catch (e: Exception) {
                // 在这里可以添加更复杂的错误处理逻辑，例如区分不同类型的异常
                mutableErrorMessage.value = "An error occurred: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 清除错误信息。
     */
    fun clearError() {
        mutableErrorMessage.value = null
    }
}
