package com.example.aikotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.aikotlin.base.BaseActivity
import com.example.aikotlin.database.NewsDatabase
import com.example.aikotlin.data.NewsLocalDataSource
import com.example.aikotlin.data.NewsRemoteDataSource
import com.example.aikotlin.data.NewsRepository
import com.example.aikotlin.databinding.ActivityMainBinding
import com.example.aikotlin.network.ApiClient
import com.example.aikotlin.viewmodel.MainViewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private val newsRepository by lazy {
        val apiService = ApiClient.apiService
        val newsDatabase = Room.databaseBuilder(
            applicationContext,
            NewsDatabase::class.java,
            "news_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        val newsRemoteDataSource = NewsRemoteDataSource(apiService)
        val newsLocalDataSource = NewsLocalDataSource(newsDatabase.newsDao())
        NewsRepository(newsRemoteDataSource, newsLocalDataSource)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置导航控制器
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                as androidx.navigation.fragment.NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigationView.setupWithNavController(navController)

        // 设置顶部的Toolbar
        setSupportActionBar(binding.toolbar)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> createViewModel(activity: FragmentActivity?, cls: Class<T>?): T {
        return when (cls) {
            MainViewModel::class.java -> MainViewModel(newsRepository)
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${cls?.name}")
        } as T
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}
