package com.example.aikotlin

import android.app.Application
import android.util.Log
import timber.log.Timber

class AIKotlinApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // 基本应用初始化，不包含依赖注入逻辑
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree());
        } else {
            Timber.plant(ReleaseTree());
        }
    }

    companion object{
        class ReleaseTree: Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                Timber.tag("sss${tag}").e(message)
            }
        }
    }
}