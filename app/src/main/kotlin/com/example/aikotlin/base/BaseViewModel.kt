package com.example.aikotlin.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.size.Dimension
import com.example.aikotlin.repository.NewsRepository
import com.example.aikotlin.repository.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class BaseViewModel() : ViewModel() {

}
