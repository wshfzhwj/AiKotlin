package com.example.aikotlin.ui.video

object VideoDataProvider {
    
    fun getVideos(): List<VideoItem> {
        return listOf(
            VideoItem(
                id = "1",
                title = "最新科技趋势分析：人工智能如何改变我们的生活",
                coverUrl = "",
                playCount = "10.2万",
                duration = "03:45",
                authorName = "科技前沿",
                authorAvatar = ""
            ),
            VideoItem(
                id = "2",
                title = "美食探店：这家网红餐厅的招牌菜太赞了",
                coverUrl = "",
                playCount = "5.8万",
                duration = "05:12",
                authorName = "吃货日记",
                authorAvatar = ""
            ),
            VideoItem(
                id = "3",
                title = "健身教程：15分钟居家锻炼计划",
                coverUrl = "",
                playCount = "8.3万",
                duration = "15:00",
                authorName = "健身达人",
                authorAvatar = ""
            ),
            VideoItem(
                id = "4",
                title = "旅行Vlog：探秘中国最美乡村",
                coverUrl = "",
                playCount = "12.5万",
                duration = "08:23",
                authorName = "旅行日记",
                authorAvatar = ""
            ),
            VideoItem(
                id = "5",
                title = "搞笑合集：猫咪的日常犯傻瞬间",
                coverUrl = "",
                playCount = "20.1万",
                duration = "02:45",
                authorName = "萌宠乐园",
                authorAvatar = ""
            ),
            VideoItem(
                id = "6",
                title = "生活小窍门：让你的家居更整洁",
                coverUrl = "",
                playCount = "6.7万",
                duration = "04:30",
                authorName = "生活达人",
                authorAvatar = ""
            ),
            VideoItem(
                id = "7",
                title = "职场技巧：如何高效沟通提升工作效率",
                coverUrl = "",
                playCount = "4.9万",
                duration = "06:15",
                authorName = "职场导师",
                authorAvatar = ""
            ),
            VideoItem(
                id = "8",
                title = "音乐推荐：2024年最值得听的新歌",
                coverUrl = "",
                playCount = "7.3万",
                duration = "03:20",
                authorName = "音乐频道",
                authorAvatar = ""
            )
        )
    }
    
    // 根据分类获取视频
    fun getVideosByCategory(category: String): List<VideoItem> {
        // 简单实现，实际应该按分类过滤
        return getVideos()
    }
}