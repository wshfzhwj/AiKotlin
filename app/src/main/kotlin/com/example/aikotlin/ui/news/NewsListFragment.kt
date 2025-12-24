package com.example.aikotlin.ui.news

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aikotlin.base.BaseFragment
import com.example.aikotlin.repository.NewsRepository
import com.example.aikotlin.data.ViewModelFactory
import com.example.aikotlin.databinding.FragmentNewsListBinding
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.viewmodel.NewsViewModel
import com.example.aikotlin.viewmodel.UiEvent
import kotlinx.coroutines.launch

class NewsListFragment : BaseFragment<FragmentNewsListBinding, NewsViewModel>(),
    CategoryTabLayout.OnCategorySelectedListener {

    private lateinit var adapter: NewsAdapter

    private val viewModelFactory by lazy { ViewModelFactory(requireActivity().applicationContext) }

    override val viewModel: NewsViewModel by viewModels { viewModelFactory }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewsListBinding {
        requireContext()
        return FragmentNewsListBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        // Initialize adapter
        adapter = NewsAdapter { article -> onNewsItemClick(article) }

        // Configure RecyclerView
        binding.newsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.newsRecyclerView.adapter = adapter

        // Configure SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }

        // Configure CategoryTabLayout
        binding.categoryTabLayout.setOnCategorySelectedListener(this)
    }

    override fun setupListeners() {
        val layoutManager = binding.newsRecyclerView.layoutManager as LinearLayoutManager
        binding.newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                // Load more when scrolled to the bottom
                if (lastVisibleItemPosition >= totalItemCount - 1 &&
                    totalItemCount > 0 &&
                    !binding.swipeRefreshLayout.isRefreshing
                ) {
                    viewModel.loadMore()
                }
            }
        })
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 只需要订阅一个 uiState Flow
                launch {
                    viewModel.uiState.collect { state ->
                        // 1. 处理加载状态
                        // SwipeRefreshLayout的加载状态
                        binding.swipeRefreshLayout.isRefreshing = state.isLoading && adapter.currentList.isNotEmpty()
                        // 初始加载时的ProgressBar状态
                        binding.progressBar.visibility = if (state.isLoading && adapter.currentList.isEmpty()) View.VISIBLE else View.GONE

                        // 2. 提交新闻列表
                        // 使用distinctUntilChanged()的特性，只有在列表引用变化时才会提交
                        if (adapter.currentList != state.articles) {
                            adapter.submitList(state.articles)
                        }

                        // 3. 处理空状态视图
                        binding.emptyStateText.visibility = if (state.articles.isEmpty() && !state.isLoading) View.VISIBLE else View.GONE

                        // 4. 处理 "没有更多数据" 的状态
                        if (!state.hasMoreData) {
                            adapter.hideLoadingFooter() // 假设Adapter有这个方法来移除底部的加载视图
                        }
                    }
                }
                // 订阅一次性UI事件
                launch {
                    viewModel.uiEvents.collect { event ->
                        when (event) {
                            is UiEvent.ShowToast -> {
                                showToast(event.message)
                            }
                        }
                    }
                }

            }
        }
    }

    private fun onNewsItemClick(article: NewsArticle) {
        val directions = NewsListFragmentDirections.actionNewsListToNewsDetail(article)
        findNavController().navigate(directions)
    }

    override fun onCategorySelected(categoryId: String, categoryName: String) {
        viewModel.setCategory(categoryId)
    }

}