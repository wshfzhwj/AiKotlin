package com.example.aikotlin.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    
    private const val BASE_URL = "https://newsapi.org/v2/"
    private const val API_KEY = "your_api_key_here" // 实际使用时应从安全存储中获取
    private const val USE_FAKE_API = true
    
    // 添加API Key的拦截器
    private val apiKeyInterceptor = Interceptor {
        val original = it.request()
        val httpUrl = original.url.newBuilder()
            .addQueryParameter("apiKey", API_KEY)
            .build()
        
        val request = original.newBuilder()
            .url(httpUrl)
            .build()
        
        it.proceed(request)
    }
    
    // 日志拦截器
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // OkHttpClient配置
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiKeyInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Gson配置
    private val gson: Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        .create()
    
    // Retrofit配置
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    // 创建API服务实例
    val apiService: ApiService by lazy {
        if (USE_FAKE_API) {
            FakeApiService()
        } else {
            retrofit.create(ApiService::class.java)
        }
    }
    
    // 创建支持Flow的API服务实例
    val flowApiService: FlowFakeApiService by lazy {
        // 使用支持Flow的模拟API服务
        FlowFakeApiService()
    }
    
    // 创建真实的Retrofit API服务
    private val realApiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}