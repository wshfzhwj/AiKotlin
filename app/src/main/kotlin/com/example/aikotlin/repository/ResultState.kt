package com.example.aikotlin.repository

sealed class ResultState<out T> {
    data class Success<out T>(val data: T) : ResultState<T>()
    data class Error(val exception: Exception, val message: String) : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
}
