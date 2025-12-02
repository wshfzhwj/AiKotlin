package com.example.aikotlin.ui.smallvideo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.aikotlin.R
import com.example.aikotlin.base.BaseFragment
import com.example.aikotlin.base.BaseViewModel
import com.example.aikotlin.databinding.FragmentSmallVideoBinding
import kotlin.getValue

class SmallVideoFragment : BaseFragment<FragmentSmallVideoBinding, BaseViewModel>() {
    override val viewModel: BaseViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SmallVideoAdapter
    private lateinit var progressBar: View
    private lateinit var emptyStateView: View


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)
        emptyStateView = view.findViewById(R.id.emptyStateView)

        // 设置垂直滑动
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            context,
            RecyclerView.VERTICAL,
            false
        )

        adapter = SmallVideoAdapter()
        recyclerView.adapter = adapter

        loadSmallVideos()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSmallVideoBinding {
        return FragmentSmallVideoBinding.inflate(inflater, container, false)
    }

    private fun loadSmallVideos() {
        // 显示加载状态
        progressBar.visibility = View.VISIBLE
        emptyStateView.visibility = View.GONE

        // 模拟加载延迟
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val videos = SmallVideoDataProvider.getSmallVideos()
            adapter.submitList(videos)

            // 隐藏加载状态
            progressBar.visibility = View.GONE

            // 如果没有数据，显示空状态
            emptyStateView.visibility = if (videos.isEmpty()) View.VISIBLE else View.GONE
        }, 1000)
    }
}