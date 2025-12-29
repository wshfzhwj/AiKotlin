package com.example.aikotlin.base

import android.util.Log
import com.example.aikotlin.repository.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

/**
 * A base class for repositories, providing a helper function to safely execute API calls
 * and wrap them in a ResultState Flow.
 */
abstract class BaseRepository {

    /**
     * Executes a suspend function and wraps its result in a Flow<ResultState<T>>.
     * It emits Loading, then either Success with the data or Error with an exception.
     */
    fun <T> safeApiCall(block: suspend () -> T): Flow<ResultState<T>> = flow {
        // 1. 开始时，自动发射 Loading
        emit(ResultState.Loading)
        try {
            // 2. 执行真正的网络请求
            val response = block()
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            // 4. 捕获所有异常，转换为 Error 状态
            // 这里可以做全局错误映射，比如解析 HttpException 的 code 401/500 等
            emit(ResultState.Error(e, resolveError(e)))
        }
    }.flowOn(Dispatchers.IO)

    // 简单的错误解析示例
     fun resolveError(e: Exception): String {
        return when (e) {
            is java.net.SocketTimeoutException -> "网络连接超时"
            is java.net.UnknownHostException -> "无网络连接"
            else -> e.message ?: "未知错误"
        }
    }

    /**
     * 3. 新增：高级缓存策略封装 (NetworkBoundResource)
     * 核心思想：UI 永远只观察数据库(Flow)，网络请求只是为了更新数据库。
     *
     * @param queryFromDb 从数据库查询数据 (返回 Flow，通常是 Room 生成的)
     * @param fetchNetwork 从网络获取数据 (挂起函数)
     * @param saveCallResult 将网络数据保存到数据库 (挂起函数)
     * @param shouldFetch 是否需要从网络获取 (可选，默认总是获取)
     */
    protected inline fun <reified T> networkBoundResourceFlowGPT(
        crossinline queryFromDb: suspend () -> Flow<T>,
        crossinline fetchNetwork: suspend () -> T,
        crossinline saveCallResult: suspend (T) -> Unit,
        crossinline shouldFetch: (T?) -> Boolean = {
            when (it) {
                null -> true
                is Collection<*> -> it.isEmpty()
                else -> false
            }
        }
    ): Flow<ResultState<T>> = flow {
        try {
            // 1. 发射 Loading
            emit(ResultState.Loading)
//         2. 先查缓存
//         first() 会挂起直到获取到数据库的当前最新值
//         注意：这里不用 collect，因为我们只取一次当前状态来判断是否需要请求网络
            val dbData = queryFromDb().firstOrNull()
            // 需要请求：先发射当前的缓存数据（让用户先看着旧数据，别显示空白）
            // 3. 判断是否需要请求网络
            if (dbData != null && !shouldFetch(dbData)) {
                emit(ResultState.Success(dbData))
                return@flow
            }
            val remoteData = try {
                // 3.2 执行网络请求
                fetchNetwork()
                // 3.3 保存到数据库 (Room 会自动通知 queryFromDb 的观察者)
//                emit(ResultState.Success(remoteData) )
            } catch (e: Exception) {
                // 3.4 网络失败：发射错误，但数据依然是缓存数据
                if (dbData != null) {
                    emit(ResultState.Success(dbData))
                    return@flow
                } else {
                    emit(ResultState.Error(e, resolveError(e)))
                }
            }
            if (remoteData != null && remoteData is T) {
                // 4. 保存到数据库
                try {
                    saveCallResult(remoteData)
                    emit(ResultState.Success(remoteData))
                } catch (e: Exception) {
                    // 保存失败不影响数据返回，但可以记录日志
                    emit(ResultState.Error(e, resolveError(e)))
                }
            } else if (dbData != null) {
                // 5. API没有数据，但数据库有数据
                emit(ResultState.Success(dbData))
            } else {
                // 6. 都没有数据
                val e = Exception("No data available")
                emit(ResultState.Error(e, resolveError(e)))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e, resolveError(e)))
        }
    }

    /**
     * 3. 新增：高级缓存策略封装 (NetworkBoundResource)
     * 核心思想：UI 永远只观察数据库(Flow)，网络请求只是为了更新数据库。
     *
     * @param queryFromDb 从数据库查询数据 (返回 Flow，通常是 Room 生成的)
     * @param fetchNetwork 从网络获取数据 (挂起函数)
     * @param saveCallResult 将网络数据保存到数据库 (挂起函数)
     * @param shouldFetch 是否需要从网络获取 (可选，默认总是获取)
     */
    protected inline fun <reified ResultType, RequestType> networkBoundResourceFlowEmitAll(
        crossinline queryFromDb: suspend () -> Flow<ResultType>,
        crossinline fetchNetwork: suspend () -> RequestType,
        crossinline saveCallResult: suspend (RequestType) -> Unit,
        crossinline shouldFetch: (ResultType?) -> Boolean = { true },
        crossinline typeMap: (RequestType) -> ResultType
    ): Flow<ResultState<ResultType>> = flow {
        try {
            // 1. 发射 Loading
            emit(ResultState.Loading)
//         2. 先查缓存
//         first() 会挂起直到获取到数据库的当前最新值
//         注意：这里不用 collect，因为我们只取一次当前状态来判断是否需要请求网络
            val dbData = queryFromDb().first()
            // 需要请求：先发射当前的缓存数据（让用户先看着旧数据，别显示空白）
            // 3. 判断是否需要请求网络
            if (shouldFetch(dbData)) {
                emit(ResultState.Success(dbData))
                try {
                    // 3.2 执行网络请求
                    val remoteData = fetchNetwork()
                    // 3.3 保存到数据库 (Room 会自动通知 queryFromDb 的观察者)
                    saveCallResult(remoteData)
                } catch (e: Exception) {
                    // 3.4 网络失败：发射错误，但数据依然是缓存数据
                    emit(ResultState.Error(e, resolveError(e)))
                }
            } else {

            }
            emitAll(queryFromDb().map { ResultState.Success(it) })
        } catch (e: Exception) {
            emit(ResultState.Error(e, resolveError(e)))
        }
    }



    /**
     * 3. 新增：高级缓存策略封装 (NetworkBoundResource)
     * 核心思想：UI 永远只观察数据库(Flow)，网络请求只是为了更新数据库。
     *
     * @param queryFromDb 从数据库查询数据 (返回 Flow，通常是 Room 生成的)
     * @param fetchNetwork 从网络获取数据 (挂起函数)
     * @param saveCallResult 将网络数据保存到数据库 (挂起函数)
     * @param shouldFetch 是否需要从网络获取 (可选，默认总是获取)
     */
    protected inline fun <ResultType, RequestType> networkBoundResourceFlow(
        crossinline queryFromDb: suspend () -> Flow<ResultType>,
        crossinline fetchNetwork: suspend () -> RequestType,
        crossinline saveCallResult: suspend (RequestType) -> Unit,
        crossinline shouldFetch: (ResultType) -> Boolean = { true },
        crossinline typeMap: (RequestType) -> ResultType
    ): Flow<ResultState<ResultType>> = flow {
        // 1. 发射 Loading
        emit(ResultState.Loading)
//         2. 先查缓存
//         first() 会挂起直到获取到数据库的当前最新值
//         注意：这里不用 collect，因为我们只取一次当前状态来判断是否需要请求网络
        val dbData = queryFromDb().first()
        // 需要请求：先发射当前的缓存数据（让用户先看着旧数据，别显示空白）
        emit(ResultState.Success(dbData))
        // 3. 判断是否需要请求网络
        if (shouldFetch(dbData)) {
            try {
                // 3.2 执行网络请求
                val remoteData = fetchNetwork()
                // 3.3 保存到数据库 (Room 会自动通知 queryFromDb 的观察者)
                saveCallResult(remoteData)
                emit(ResultState.Success(typeMap(remoteData)))
            } catch (e: Exception) {
                // 3.4 网络失败：发射错误，但数据依然是缓存数据
                emit(ResultState.Error(e, resolveError(e)))
            }
        } else {
            // 不需要网络请求，直接用缓存
        }
    }

    /**
     * 增量更新版本 - 先返回数据库数据，然后尝试从API更新
     */
    fun <T> fetchDataIncremental(
        queryFromDb: suspend () -> Flow<T>,
        fetchNetwork: suspend () -> T?,
        saveCallResult: suspend (T) -> Unit,
        shouldFetch: (T?) -> Boolean = { true }
    ): Flow<ResultState<T>> = channelFlow {
        try {
            // 发射加载状态
            send(ResultState.Loading)
            // 1. 先发射数据库数据
            queryFromDb()
                .catch { e ->
                    // 数据库错误，继续尝试API
                    println("Database error: ${e.message}")
                }
                .collect { dbData ->
                    // 发射数据库数据
                    send(ResultState.Success(dbData))
                    // 如果应该从API更新
                    if (shouldFetch(dbData)) {
                        try {
                            // 2. 从API获取最新数据
                            val apiData = fetchNetwork()
                            if (apiData != null) {
                                // 保存到数据库
                                saveCallResult(apiData)
                                // 发射更新后的数据
                                send(ResultState.Success(apiData))
                            }
                        } catch (e: Exception) {
                            // API错误，但已经有数据库数据，不发射错误
                            println("API error: ${e.message}")
                        }
                    }
                }

        } catch (e: Exception) {
            send(ResultState.Error(e,resolveError(e)))
        }
    }

    /**
     * 3. 新增：高级缓存策略封装 (NetworkBoundResource)
     * 核心思想：UI 永远只观察数据库(Flow)，网络请求只是为了更新数据库。
     *
     * @param queryFromDb 从数据库查询数据 (返回 Flow，通常是 Room 生成的)
     * @param fetchNetwork 从网络获取数据 (挂起函数)
     * @param saveCallResult 将网络数据保存到数据库 (挂起函数)
     * @param shouldFetch 是否需要从网络获取 (可选，默认总是获取)
     */
    protected inline fun <ResultType, RequestType> networkBoundResource(
        crossinline queryFromDb: suspend () -> Flow<ResultType>,
        crossinline fetchNetwork: suspend () -> RequestType,
        crossinline saveCallResult: suspend (RequestType) -> Unit,
        crossinline shouldFetch: (ResultType?) -> Boolean = { true }
    ): Flow<ResultState<ResultType>> = channelFlow { // 1. 使用 channelFlow
        // 发射初始 Loading
        send(ResultState.Loading)
        // 标记位：确保只在第一次收到数据时判断是否请求网络
        var isFirstCollection = true
        // 2. 启动收集数据库的逻辑 (这是唯一的一次订阅)
        // 这里的 collect 会一直运行，直到外部协程取消
        queryFromDb().collect { dbData ->
            // 3. 总是第一时间把数据库的数据发射出去 (SSOT)
            send(ResultState.Success(dbData))
            // 4. 判断是否是第一次数据，且需要网络请求
            if (isFirstCollection && shouldFetch(dbData)) {
                isFirstCollection = false
                // 5. 关键优化点：启动一个新的子协程去跑网络
                // 这样网络请求挂起时，不会阻塞外层的 collect 循环
                // 如果此时数据库被其他地方更新了，collect 依然能立刻收到新数据并 send 出去
                launch {
                    try {
                        val remoteData = fetchNetwork()
                        saveCallResult(remoteData)
                        // 保存后，Room 会自动通知上面的 collect 收到新数据，从而再次 send(Success)
                    } catch (e: Exception) {
                        // 网络失败，发射 Error，但注意：
                        // 此时流并没有断，用户依然能看到之前的缓存数据 (dbData)
                        send(ResultState.Error(e, resolveError(e)))
                    }
                }
            } else {
                // 后续的数据库更新，只单纯负责转发，不再触发网络请求
                isFirstCollection = false
            }
        }
    }
}
