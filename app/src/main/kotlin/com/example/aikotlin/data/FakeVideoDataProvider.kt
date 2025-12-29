package com.example.aikotlin.data

import com.example.aikotlin.model.Category
import com.example.aikotlin.model.Video

object FakeVideoDataProvider {

    private val videoCategories = listOf(
        Category("hot", "热门"),
        Category("music", "音乐"),
        Category("funny", "搞笑"),
        Category("entertainment", "娱乐"),
        Category("tech", "科技")
    )

    private val videos = mapOf(
        "hot" to listOf(
            Video(1, "热门视频1", "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4", "https://i.ytimg.com/vi/aqz-KE-bpKQ/maxresdefault.jpg"),
            Video(2, "热门视频2", "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4", "https://i.ytimg.com/vi/aqz-KE-bpKQ/maxresdefault.jpg")
        ),
        "music" to listOf(
            Video(3, "音乐视频1", "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4", "https://i.ytimg.com/vi/aqz-KE-bpKQ/maxresdefault.jpg")
        ),
        "funny" to emptyList(), // 搞笑分类暂无视频
        "entertainment" to listOf(
            Video(4, "娱乐视频1", "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4", "https://i.ytimg.com/vi/aqz-KE-bpKQ/maxresdefault.jpg")
        ),
        "tech" to listOf(
            Video(5, "科技视频1", "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4", "https://i.ytimg.com/vi/aqz-KE-bpKQ/maxresdefault.jpg"),
            Video(6, "科技视频2", "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4", "https://i.ytimg.com/vi/aqz-KE-bpKQ/maxresdefault.jpg")
        )
    )

    fun getVideoCategories(): List<Category> = videoCategories

    fun getVideos(categoryId: String = "hot"): List<Video> = videos[categoryId] ?: emptyList()
}
