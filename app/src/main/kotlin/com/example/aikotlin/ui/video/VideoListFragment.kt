package com.example.aikotlin.ui.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aikotlin.base.BaseFragment
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.data.FakeNewsDataProvider
import com.example.aikotlin.data.FakeVideoDataProvider
import com.example.aikotlin.databinding.FragmentVideoListBinding
import com.example.aikotlin.ui.news.CategoryTabLayout
import kotlinx.coroutines.launch

class VideoListFragment : BaseFragment<FragmentVideoListBinding, BaseViewModel>(),
    CategoryTabLayout.OnCategorySelectedListener {

    override val viewModel: BaseViewModel by viewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVideoListBinding {
        return FragmentVideoListBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化适配器
        val videoAdapter = VideoAdapter(requireContext())

        // 设置RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = videoAdapter

        // 设置分类栏
        binding.categoryTabLayout.setCategories(FakeVideoDataProvider.getVideoCategories())
        binding.categoryTabLayout.setOnCategorySelectedListener(this)

        // 初始加载默认分类的数据
        loadVideoData(videoAdapter)
    }

    /**
     * 根据分类ID加载视频数据。
     * @param categoryId 分类ID，默认为“hot”（热门）。
     */
    private fun loadVideoData(adapter: VideoAdapter, categoryId: String = "hot") {
        lifecycleScope.launch {
            // 开始加载，显示进度条
            binding.progressBar.isVisible = true
            binding.emptyState.isVisible = false

            // 模拟网络延迟
            kotlinx.coroutines.delay(500)

            // 获取并提交视频列表
            val videoList = FakeVideoDataProvider.getVideos(categoryId)
            adapter.submitList(videoList)

            // 加载完成，隐藏进度条并根据结果显示或隐藏空状态
            binding.progressBar.isVisible = false
            binding.emptyState.isVisible = videoList.isEmpty()
        }
    }

    /**
     * 当分类被选中时的回调。
     */
    override fun onCategorySelected(categoryId: String, categoryName: String) {
        // 确保adapter已初始化
        (binding.recyclerView.adapter as? VideoAdapter)?.let {
            loadVideoData(it, categoryId)
        }
    }
}