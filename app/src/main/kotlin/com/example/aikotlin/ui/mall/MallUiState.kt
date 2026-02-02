package com.example.aikotlin.ui.mall

import com.example.aikotlin.model.MallHomeData

sealed class MallUiState {
    object Loading : MallUiState()
    data class Success(val data: MallHomeData) : MallUiState()
    data class Error(val message: String) : MallUiState()
}
