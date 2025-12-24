package com.example.aikotlin

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.aikotlin.base.BaseActivity
import com.example.aikotlin.database.NewsDatabase
import com.example.aikotlin.databinding.ActivityMainBinding
import com.example.aikotlin.repository.MainRepository
import com.example.aikotlin.viewmodel.MainViewModel
import com.example.aikotlin.viewmodel.NewsViewModel
import okhttp3.internal.http2.Http2Reader

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpDataBase()
        // 设置导航控制器
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        // 设置顶部的Toolbar
        setSupportActionBar(binding.toolbar)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setUpDataBase() {
        Room.databaseBuilder(
            applicationContext,
            NewsDatabase::class.java,
            "news_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

}
