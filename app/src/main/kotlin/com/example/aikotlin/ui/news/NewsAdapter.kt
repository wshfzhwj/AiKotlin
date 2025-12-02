package com.example.aikotlin.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aikotlin.R
import com.example.aikotlin.databinding.ItemLoadingMoreBinding
import com.example.aikotlin.databinding.ItemNewsArticleBinding
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.model.NewsArticleDiffCallback
import com.example.aikotlin.util.DateUtils
import coil.load

// 标记加载中项的常量
private val LOADING_ITEM = NewsArticle(
    id = "loading",
    title = "",
    content = "",
    source = "",
    author = "",
    publishedAt = java.util.Date(),
    url = "",
    urlToImage = "",
    category = "all"
)

class NewsAdapter(
    private val onItemClick: (NewsArticle) -> Unit
) : ListAdapter<NewsArticle, RecyclerView.ViewHolder>(NewsArticleDiffCallback()) {
    
    companion object {
        private const val VIEW_TYPE_NORMAL = 0
        private const val VIEW_TYPE_LOADING = 1
    }
    
    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).id == "loading") VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> {
                val binding = ItemNewsArticleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                NewsViewHolder(binding)
            }
            VIEW_TYPE_LOADING -> {
                val binding = ItemLoadingMoreBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LoadingViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NewsViewHolder) {
            val article = getItem(position)
            holder.bind(article)
        }
        // LoadingViewHolder不需要绑定数据
    }
    
    // 显示加载更多的底部视图
    fun showLoadingFooter() {
        val currentList = currentList.toMutableList()
        // 如果列表中已经有加载项，则不再添加
        if (currentList.none { it.id == "loading" }) {
            currentList.add(LOADING_ITEM)
            submitList(currentList)
        }
    }

    // 隐藏加载更多的底部视图
    fun hideLoadingFooter() {
        val currentList = currentList.toMutableList()
        val loadingIndex = currentList.indexOfFirst { it.id == "loading" }
        if (loadingIndex != -1) {
            currentList.removeAt(loadingIndex)
            submitList(currentList)
        }
    }
    
    inner class NewsViewHolder(private val binding: ItemNewsArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }
        
        fun bind(article: NewsArticle) {
            binding.apply{
                titleTextView.text = article.title
                contentTextView.text = article.content
                sourceTextView.text = article.source
                authorTextView.text = article.author ?: "未知作者"
                dateTextView.text = DateUtils.formatDate(article.publishedAt)
                
                // 对于本地模拟数据，我们使用默认图片
                // 如果有真实的网络图片URL，可以取消下面的注释并使用Coil加载
                newsImage.setImageResource(R.drawable.ic_placeholder_news_colorful)
                
                // 真实网络图片加载方式（当接入网络时使用）
                /*
                article.urlToImage?.let { imageUrl ->
                    newsImage.load(imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_placeholder_news_colorful)
                        error(R.drawable.ic_placeholder_news_colorful)
                    }
                }
                */
            }
        }
    }
    
    inner class LoadingViewHolder(binding: ItemLoadingMoreBinding) : RecyclerView.ViewHolder(binding.root) {
        private val progressBar: ProgressBar = binding.loadingMoreProgress
    }
}