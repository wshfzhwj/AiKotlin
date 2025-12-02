package com.example.aikotlin.ui.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aikotlin.R
import com.example.aikotlin.base.BaseFragment
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.databinding.FragmentVideoListBinding
import kotlinx.coroutines.launch
import kotlin.getValue

class VideoListFragment : BaseFragment<FragmentVideoListBinding, BaseViewModel>() {
    override val viewModel: BaseViewModel by viewModels()
    private lateinit var recyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var emptyState: android.widget.TextView
    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var videoAdapter: VideoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化视图
        recyclerView = view.findViewById(R.id.recyclerView)
        emptyState = view.findViewById(R.id.emptyState)
        progressBar = view.findViewById(R.id.progressBar)

        // 初始化适配器
        videoAdapter = VideoAdapter(requireContext())

        // 设置RecyclerView
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = videoAdapter

        // 加载模拟视频数据
        loadVideoData()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentVideoListBinding {
        return FragmentVideoListBinding.inflate(inflater, container, false)
    }

    private fun loadVideoData() {
        lifecycleScope.launch {
            progressBar.isVisible = true
            emptyState.isVisible = false

            // 模拟加载延迟
            kotlinx.coroutines.delay(500)

            // 使用模拟数据
            val videoList = VideoDataProvider.getVideos()
            videoAdapter.submitList(videoList)

            progressBar.isVisible = false
            emptyState.isVisible = videoList.isEmpty()
        }
    }
}