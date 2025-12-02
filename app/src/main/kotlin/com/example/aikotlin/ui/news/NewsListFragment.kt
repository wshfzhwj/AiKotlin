package com.example.aikotlin.ui.news

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
import com.example.aikotlin.data.NewsRepository
import com.example.aikotlin.data.ViewModelFactory
import com.example.aikotlin.databinding.FragmentNewsListBinding
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.viewmodel.NewsViewModel
import kotlinx.coroutines.launch

class NewsListFragment : BaseFragment<FragmentNewsListBinding, NewsViewModel>(), CategoryTabLayout.OnCategorySelectedListener {

    private lateinit var adapter: NewsAdapter

    private val viewModelFactory by lazy { ViewModelFactory(requireActivity().applicationContext) }

    override val viewModel: NewsViewModel by viewModels { viewModelFactory }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewsListBinding {
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
                launch {
                    viewModel.newsArticles.collect { articles ->
                        adapter.submitList(articles)
                        binding.emptyStateText.visibility = if (articles.isEmpty() && !viewModel.isLoading.value) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.isLoading.collect { isLoading ->
                        // Only show progress bar if not refreshing (SwipeRefreshLayout handles its own)
                        // and if it's the initial load (empty list)
                        if (isLoading && !binding.swipeRefreshLayout.isRefreshing && adapter.currentList.isEmpty()) {
                            binding.progressBar.visibility = View.VISIBLE
                        } else {
                            binding.progressBar.visibility = View.GONE
                        }

                        if (!isLoading) {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }

                launch {
                    viewModel.hasMoreData.collect { hasMore ->
                        if (!hasMore) {
                            adapter.hideLoadingFooter()
                        }
                    }
                }
                
                launch {
                    viewModel.errorMessage.collect { error ->
                        error?.let {
                            showToast(it)
                            viewModel.clearError()
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
//        viewModel.setCategory(categoryId)
        viewModel.setCategory("all")
    }

}