package com.example.aikotlin.ui.micro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aikotlin.R
import com.example.aikotlin.base.BaseFragment
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.databinding.FragmentMicroNewsBinding
import com.example.aikotlin.viewmodel.NewsViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class NewsMicroFragment : BaseFragment<FragmentMicroNewsBinding, NewsViewModel>() {
    override val viewModel: NewsViewModel by viewModels()
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var emptyState: android.widget.TextView
    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var microAdapter: MicroNewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 初始化视图
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyState = view.findViewById(R.id.emptyState)
        progressBar = view.findViewById(R.id.progressBar)
        
        // 初始化适配器
        microAdapter = MicroNewsAdapter()
        
        // 设置RecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = microAdapter
        
        // 加载模拟微头条数据
        loadMicroNewsData()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMicroNewsBinding {
        return  FragmentMicroNewsBinding.inflate(inflater, container, false)
    }

    private fun loadMicroNewsData() {
        lifecycleScope.launch {
            progressBar.isVisible = true
            emptyState.isVisible = false
            
            // 模拟加载延迟
            kotlinx.coroutines.delay(500)
            
            // 使用模拟数据
            val microNewsList = MicroNewsDataProvider.getMicroNews()
            microAdapter.submitList(microNewsList)
            
            progressBar.isVisible = false
            emptyState.isVisible = microNewsList.isEmpty()
        }
    }
}