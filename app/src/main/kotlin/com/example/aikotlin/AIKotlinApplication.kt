package com.example.aikotlin

import android.app.Application

class AIKotlinApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 基本应用初始化，不包含依赖注入逻辑
    }
}