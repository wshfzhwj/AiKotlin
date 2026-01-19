package com.example.aikotlin.ui.news

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aikotlin.R
import com.example.aikotlin.base.BaseFragment
import com.example.aikotlin.data.FakeNewsDataProvider
import com.example.aikotlin.data.ViewModelFactory
import com.example.aikotlin.databinding.FragmentNewsListBinding
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.viewmodel.NewsViewModel
import com.example.aikotlin.viewmodel.UiEvent
import kotlinx.coroutines.launch
import timber.log.Timber

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
        binding.categoryTabLayout.setCategories(FakeNewsDataProvider.getCategories())
        binding.categoryTabLayout.setOnCategorySelectedListener(this)
    }

    override fun setupListeners() {
        // RecyclerView scroll listener for pagination
        binding.newsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy <= 0 || isLoading() || !viewModel.uiState.value.hasMoreData) {
                    return
                }
                val layoutManager = binding.newsRecyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                // Load more when scrolled to the bottom
                if (lastVisibleItemPosition >= totalItemCount - 1 && totalItemCount > 0) {
                    viewModel.loadMore()
                }
            }
        })
        setupSearch()
    }

    fun isLoading(): Boolean{
        return viewModel.uiState.value.isLoading || viewModel.uiState.value.isRefreshing || viewModel.uiState.value.isLoadingMore
    }

    private fun setupSearch() {
        val searchItem = binding.toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as? SearchView

        searchView?.let {
            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // When user submits the search
                    if (!query.isNullOrBlank()) {
                        // Let the ViewModel handle the search logic
                        viewModel.searchNews(query)
                        it.clearFocus() // Hide keyboard and collapse the view
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    // Not used for now, can be implemented for real-time search
                    return true
                }
            })
        }

        // Add a listener to refresh the list when the search is closed
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                // Called when search view is expanded
                return true // Return true to allow expanding
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                // Called when search view is collapsed
                // Refresh the list to show the original data for the selected category
                viewModel.refresh()
                return true // Return true to allow collapsing
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
                        binding.swipeRefreshLayout.isRefreshing = state.isRefreshing
                        // 初始加载时的ProgressBar状态
                        binding.progressBar.visibility =
                            if (state.isLoadingMore) View.VISIBLE else View.GONE

                        // 2. 提交新闻列表
                        // 使用distinctUntilChanged()的特性，只有在列表引用变化时才会提交
                        if (adapter.currentList != state.articles) {
                            adapter.submitList(state.articles)
                        }

                        // 3. 处理空状态视图
                        binding.emptyStateText.visibility =
                            if (state.articles.isEmpty() && !state.isLoading) View.VISIBLE else View.GONE

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
                                showToast(event.message!!)
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
