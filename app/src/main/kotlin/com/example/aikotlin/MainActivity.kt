package com.example.aikotlin

import android.os.Bundle
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

    override fun <T : ViewModel> createViewModel(activity: FragmentActivity?, cls: Class<T>?): T {
        if (cls!!.isAssignableFrom(MainViewModel::class.java)) {
            // 如果是 NewsViewModel，我们就知道如何创建它
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(MainRepository()) as T
        }

        // if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
        //     @Suppress("UNCHECKED_CAST")
        //     return MainViewModel(newsRepository) as T
        // }

        // 如果是未知的 ViewModel 类型，抛出异常
        throw IllegalArgumentException("Unknown ViewModel class: ${cls.name}")
    }

}
