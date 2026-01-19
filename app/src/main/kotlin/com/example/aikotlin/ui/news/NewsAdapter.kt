package com.example.aikotlin.ui.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.aikotlin.R
import com.example.aikotlin.databinding.ItemNewsArticleBinding
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.utils.DateUtils

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

// 假设 NewsAdapter 是一个 ListAdapter
class NewsAdapter(private val onItemClick: (NewsArticle) -> Unit) :
    ListAdapter<NewsArticle, RecyclerView.ViewHolder>(NewsArticleDiffCallback()) {

    // 假设你有不同的 View Type
    private val ITEM_VIEW_TYPE_NEWS = 0
    private val ITEM_VIEW_TYPE_LOADING = 1

    private var showLoadingFooter = false
    private var hasMoreData = true // 可以根据ViewModel的hasMoreData来设置

    // NewsArticleDiffCallback
    class NewsArticleDiffCallback : DiffUtil.ItemCallback<NewsArticle>() {
        override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem == newItem
        }
    }

    // ViewHolder for news items
    class NewsItemViewHolder(private val binding: ItemNewsArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: NewsArticle, onItemClick: (NewsArticle) -> Unit) {
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
                root.setOnClickListener { onItemClick(article) }
            }
        }
    }

    // ViewHolder for loading footer
    class LoadingViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1 && showLoadingFooter) {
            ITEM_VIEW_TYPE_LOADING
        } else {
            ITEM_VIEW_TYPE_NEWS
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_NEWS -> {
                val binding = ItemNewsArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NewsItemViewHolder(binding)
            }
            ITEM_VIEW_TYPE_LOADING -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading_more, parent, false)
                LoadingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == ITEM_VIEW_TYPE_NEWS) {
            (holder as NewsItemViewHolder).bind(getItem(position), onItemClick)
        }
        // No binding needed for LoadingViewHolder
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + (if (showLoadingFooter) 1 else 0)
    }

    // Call this from Fragment to show the loading footer
    fun showLoadingFooter() {
        if (!showLoadingFooter) {
            showLoadingFooter = true
            // 通知适配器新增了一个项（加载视图）
            notifyItemInserted(super.getItemCount()) // 注意：是super.getItemCount()
        }
    }

    // Call this from Fragment to hide the loading footer
    fun hideLoadingFooter() {
        if (showLoadingFooter) {
            showLoadingFooter = false
            // 通知适配器移除了一个项（加载视图）
            notifyItemRemoved(super.getItemCount()) // 注意：是super.getItemCount()
        }
    }

    // 提交列表时，确保加载 footer 的状态不会被 ListAdapter 自身的 submitList 干扰
    override fun submitList(list: List<NewsArticle>?) {
        super.submitList(list)
        // 在新的数据提交后，确保 loading footer 的状态是根据 ViewModel 状态更新的
        // 这里只是为了演示，实际逻辑由 Fragment 的 observeData 负责
    }
}