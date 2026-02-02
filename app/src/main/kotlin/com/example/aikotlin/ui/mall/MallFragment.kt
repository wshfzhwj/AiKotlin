package com.example.aikotlin.ui.mall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
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

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (mallAdapter.getItemViewType(position)) {
                    MallAdapter.TYPE_PRODUCT_CARD -> 1
                    else -> 2 // Banner, Category Grid, and Seckill take full width
                }
            }
        }

        binding.rvMallContent.apply {
            layoutManager = gridLayoutManager
            adapter = mallAdapter
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MallUiState.Loading -> {
                            binding.pbLoading.visibility = View.VISIBLE
                        }
                        is MallUiState.Success -> {
                            binding.pbLoading.visibility = View.GONE
                            val items = mutableListOf<MallItem>()
                            items.add(MallItem.Banner(state.data.banners))
                            items.add(MallItem.CategoryGrid(state.data.categories))
                            items.add(MallItem.Seckill(state.data.seckillProducts))
                            state.data.feedProducts.forEach {
                                items.add(MallItem.ProductCard(it))
                            }
                            mallAdapter.submitList(items)
                        }
                        is MallUiState.Error -> {
                            binding.pbLoading.visibility = View.GONE
                            showToast(state.message)
                        }
                    }
                }
            }
        }
    }
}
