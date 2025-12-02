package com.example.aikotlin.ui.micro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aikotlin.R

// 微头条数据类
data class MicroNewsItem(
    val id: String,
    val content: String,
    val imageList: List<String>,
    val authorName: String,
    val authorAvatar: String,
    val publishTime: String,
    val likeCount: Int,
    val commentCount: Int,
    val shareCount: Int
)

class MicroNewsAdapter : ListAdapter<MicroNewsItem, MicroNewsAdapter.MicroNewsViewHolder>(MicroNewsDiffCallback()) {

    class MicroNewsViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val authorAvatar: android.widget.ImageView = itemView.findViewById(R.id.authorAvatar)
        val authorName: android.widget.TextView = itemView.findViewById(R.id.authorName)
        val publishTime: android.widget.TextView = itemView.findViewById(R.id.publishTime)
        val content: android.widget.TextView = itemView.findViewById(R.id.content)
        val imageContainer: android.widget.LinearLayout = itemView.findViewById(R.id.imageContainer)
        val likeCount: android.widget.TextView = itemView.findViewById(R.id.likeCount)
        val commentCount: android.widget.TextView = itemView.findViewById(R.id.commentCount)
        val shareCount: android.widget.TextView = itemView.findViewById(R.id.shareCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MicroNewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_micro_news, parent, false)
        return MicroNewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: MicroNewsViewHolder, position: Int) {
        val microNews = getItem(position)
        
        holder.authorName.text = microNews.authorName
        holder.publishTime.text = microNews.publishTime
        holder.content.text = microNews.content
        holder.likeCount.text = microNews.likeCount.toString()
        holder.commentCount.text = microNews.commentCount.toString()
        holder.shareCount.text = microNews.shareCount.toString()
        
        // 使用占位图
        holder.authorAvatar.setImageResource(R.drawable.ic_placeholder_avatar)
        
        // 设置图片（简化版，实际应该根据图片数量动态调整布局）
        holder.imageContainer.removeAllViews()
        microNews.imageList.take(3).forEach { _ ->
            val imageView = android.widget.ImageView(holder.itemView.context)
            imageView.layoutParams = android.widget.LinearLayout.LayoutParams(
                100,
                100
            ).apply {
                setMargins(4, 0, 4, 0)
            }
            imageView.setImageResource(R.drawable.ic_placeholder_news)
            holder.imageContainer.addView(imageView)
        }
        
        // 隐藏没有图片的容器
        holder.imageContainer.visibility = if (microNews.imageList.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE
    }

    class MicroNewsDiffCallback : DiffUtil.ItemCallback<MicroNewsItem>() {
        override fun areItemsTheSame(oldItem: MicroNewsItem, newItem: MicroNewsItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MicroNewsItem, newItem: MicroNewsItem): Boolean {
            return oldItem == newItem
        }
    }
}