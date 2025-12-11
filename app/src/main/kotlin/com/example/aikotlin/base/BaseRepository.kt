package com.example.aikotlin.base

import com.example.aikotlin.repository.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * A base class for repositories, providing a helper function to safely execute API calls
 * and wrap them in a ResultState Flow.
 */
abstract class BaseRepository {

    /**
     * Executes a suspend function and wraps its result in a Flow<ResultState<T>>.
     * It emits Loading, then either Success with the data or Error with an exception.
     */
     fun <T> safeApiCall(apiCall: suspend () -> T): Flow<ResultState<T>> = flow {
        emit(ResultState.Loading)
        try {
            emit(ResultState.Success(apiCall()))
        } catch (e: Exception) {
            emit(ResultState.Error(e))
        }
    }.flowOn(Dispatchers.IO)
}
