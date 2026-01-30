package com.example.aikotlin.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.repository.LoginRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

sealed class LoginUiEvent {
    object NavigateToHome : LoginUiEvent()
    data class ShowSnackbar(val message: String) : LoginUiEvent()
}

class LoginViewModel(
    private val loginRepository: LoginRepository
) : BaseViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { LoginUiState.Loading }
            loginRepository.login(email, password).collect { result ->
                result.onSuccess {
                    _uiState.update { LoginUiState.Success }
                    _uiEvent.emit(LoginUiEvent.NavigateToHome)
                }.onFailure { exception ->
                    val errorMessage = exception.message ?: "Unknown error occurred"
                    _uiState.update { LoginUiState.Error(errorMessage) }
                    _uiEvent.emit(LoginUiEvent.ShowSnackbar(errorMessage))
                }
            }
        }
    }
}
