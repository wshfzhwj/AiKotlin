package com.example.aikotlin.ui.mall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aikotlin.base.BaseFragment
import com.example.aikotlin.databinding.FragmentMallBinding
import kotlinx.coroutines.launch

class MallFragment : BaseFragment<FragmentMallBinding, MallViewModel>() {

    override val viewModel: MallViewModel by viewModels()

    private val mallAdapter by lazy { MallAdapter() }

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMallBinding {
        return FragmentMallBinding.inflate(inflater, container, false)
    }

    override fun initViews() {
        setupRecyclerView()
        handleWindowInsets()
        setupSwipeRefresh()
    }

    private fun handleWindowInsets() {
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.viewHeaderBg) { view, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, 0)
            // Increase background height to cover status bar
            val params = view.layoutParams
            params.height = 120 + systemBars.top // 120dp is the original height
            view.layoutParams = params
            insets
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE // Prevent jumping

        binding.rvMallContent.apply {
            this.layoutManager = layoutManager
            adapter = mallAdapter
            itemAnimator = null // Prevent flashing on refresh
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy <= 0) return

                    val staggeredLayoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val lastVisibleItemPositions = staggeredLayoutManager.findLastVisibleItemPositions(null)
                    val lastVisibleItemPosition = lastVisibleItemPositions.maxOrNull() ?: 0
                    val totalItemCount = staggeredLayoutManager.itemCount

                    if (lastVisibleItemPosition >= totalItemCount - 2) {
                        viewModel.loadMore()
                    }
                    
                    // Invalidate item decorations if needed or handle scroll events
                    staggeredLayoutManager.invalidateSpanAssignments()
                }
            })
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    // Handle refreshing state
                    binding.swipeRefreshLayout.isRefreshing = state.isRefreshing
                    
                    // Handle initial loading
                    binding.pbLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    
                    // Handle data
                    state.data?.let { data ->
                        val items = mutableListOf<MallItem>()
                        if (data.banners.isNotEmpty()) items.add(MallItem.Banner(data.banners))
                        if (data.categories.isNotEmpty()) items.add(MallItem.CategoryGrid(data.categories))
                        if (data.seckillProducts.isNotEmpty()) items.add(MallItem.Seckill(data.seckillProducts))
                        
                        data.feedProducts.forEach {
                            items.add(MallItem.ProductCard(it))
                        }

                        if (state.isLoadingMore) {
                            items.add(MallItem.LoadingMore)
                        }
                        
                        mallAdapter.submitList(items)
                    }

                    // Handle error
                    state.errorMessage?.let {
                        if (!state.isLoadingMore && !state.isRefreshing) {
                            showToast(it)
                        }
                    }
                }
            }
        }
    }
}