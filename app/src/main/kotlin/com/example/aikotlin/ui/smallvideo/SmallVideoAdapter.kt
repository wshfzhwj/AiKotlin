package com.example.aikotlin.ui.smallvideo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aikotlin.R

// 小视频数据类
data class SmallVideoItem(
    val id: String,
    val title: String,
    val coverUrl: String,
    val playCount: Int,
    val authorName: String,
    val authorAvatar: String,
    val duration: Int // 秒
)

class SmallVideoAdapter : ListAdapter<SmallVideoItem, SmallVideoAdapter.SmallVideoViewHolder>(SmallVideoDiffCallback()) {

    class SmallVideoViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val coverImage: android.widget.ImageView = itemView.findViewById(R.id.coverImage)
        val playButton: android.widget.ImageView = itemView.findViewById(R.id.playButton)
        val title: android.widget.TextView = itemView.findViewById(R.id.title)
        val authorAvatar: android.widget.ImageView = itemView.findViewById(R.id.authorAvatar)
        val authorName: android.widget.TextView = itemView.findViewById(R.id.authorName)
        val playCount: android.widget.TextView = itemView.findViewById(R.id.playCount)
        val duration: android.widget.TextView = itemView.findViewById(R.id.duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallVideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_small_video, parent, false)
        return SmallVideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: SmallVideoViewHolder, position: Int) {
        val video = getItem(position)
        
        holder.title.text = video.title
        holder.authorName.text = video.authorName
        holder.playCount.text = formatPlayCount(video.playCount)
        holder.duration.text = formatDuration(video.duration)
        
        // 使用占位图
        holder.coverImage.setImageResource(R.drawable.ic_placeholder_video)
        holder.authorAvatar.setImageResource(R.drawable.ic_placeholder_avatar)
        
        // 绑定点击事件
        holder.itemView.setOnClickListener {
            // TODO: 处理视频点击播放
        }
    }

    private fun formatPlayCount(count: Int): String {
        return if (count >= 10000) {
            "${count / 10000}.${count % 10000 / 1000}万"
        } else {
            count.toString()
        }
    }

    private fun formatDuration(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "${minutes}:${String.format("%02d", remainingSeconds)}"
    }

    class SmallVideoDiffCallback : DiffUtil.ItemCallback<SmallVideoItem>() {
        override fun areItemsTheSame(oldItem: SmallVideoItem, newItem: SmallVideoItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SmallVideoItem, newItem: SmallVideoItem): Boolean {
            return oldItem == newItem
        }
    }
}