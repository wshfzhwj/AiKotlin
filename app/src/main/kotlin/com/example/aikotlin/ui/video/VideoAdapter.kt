package com.example.aikotlin.ui.video

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.aikotlin.R

// 视频数据类
data class VideoItem(
    val id: String,
    val title: String,
    val coverUrl: String,
    val playCount: String,
    val duration: String,
    val authorName: String,
    val authorAvatar: String
)

class VideoAdapter(private val context: Context) : ListAdapter<VideoItem, VideoAdapter.VideoViewHolder>(VideoDiffCallback()) {

    class VideoViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val videoTitle: android.widget.TextView = itemView.findViewById(R.id.videoTitle)
        val videoCover: android.widget.ImageView = itemView.findViewById(R.id.videoCover)
        val playCount: android.widget.TextView = itemView.findViewById(R.id.playCount)
        val duration: android.widget.TextView = itemView.findViewById(R.id.duration)
        val authorName: android.widget.TextView = itemView.findViewById(R.id.authorName)
        val authorAvatar: android.widget.ImageView = itemView.findViewById(R.id.authorAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = getItem(position)
        holder.videoTitle.text = video.title
        holder.playCount.text = video.playCount
        holder.duration.text = video.duration
        holder.authorName.text = video.authorName
        
        // 使用占位图代替实际图片加载
        holder.videoCover.setImageResource(R.drawable.ic_placeholder_video)
        holder.authorAvatar.setImageResource(R.drawable.ic_placeholder_avatar)
        
        // 点击事件
        holder.itemView.setOnClickListener {
            // TODO: 跳转到视频播放页面
        }
    }

    class VideoDiffCallback : DiffUtil.ItemCallback<VideoItem>() {
        override fun areItemsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideoItem, newItem: VideoItem): Boolean {
            return oldItem == newItem
        }
    }
}