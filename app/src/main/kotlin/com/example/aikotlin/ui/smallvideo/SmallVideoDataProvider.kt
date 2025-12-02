package com.example.aikotlin.ui.smallvideo

object SmallVideoDataProvider {
    
    fun getSmallVideos(): List<SmallVideoItem> {
        return listOf(
            SmallVideoItem(
                id = "1",
                title = "生活小妙招：10个让你生活更轻松的技巧",
                coverUrl = "cover1.jpg",
                playCount = 125600,
                authorName = "生活达人",
                authorAvatar = "avatar1.jpg",
                duration = 120 // 2分钟
            ),
            SmallVideoItem(
                id = "2",
                title = "美食制作：简单又好吃的家常菜教程",
                coverUrl = "cover2.jpg",
                playCount = 234500,
                authorName = "美食频道",
                authorAvatar = "avatar2.jpg",
                duration = 180 // 3分钟
            ),
            SmallVideoItem(
                id = "3",
                title = "旅行风景：带你看遍祖国大好河山",
                coverUrl = "cover3.jpg",
                playCount = 567800,
                authorName = "旅行家",
                authorAvatar = "avatar3.jpg",
                duration = 90 // 1分30秒
            ),
            SmallVideoItem(
                id = "4",
                title = "运动健身：在家也能锻炼的简单动作",
                coverUrl = "cover4.jpg",
                playCount = 89000,
                authorName = "健身教练",
                authorAvatar = "avatar4.jpg",
                duration = 240 // 4分钟
            ),
            SmallVideoItem(
                id = "5",
                title = "萌宠日常：可爱猫咪的有趣瞬间",
                coverUrl = "cover5.jpg",
                playCount = 1024000,
                authorName = "铲屎官",
                authorAvatar = "avatar5.jpg",
                duration = 60 // 1分钟
            )
        )
    }
}