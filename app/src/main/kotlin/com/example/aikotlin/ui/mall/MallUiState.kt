package com.example.aikotlin.ui.mall

import com.example.aikotlin.model.MallHomeData

data class MallUiState(
    val data: MallHomeData? = null,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMoreData: Boolean = true,
    val errorMessage: String? = null
)
