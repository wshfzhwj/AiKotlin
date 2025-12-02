package com.example.aikotlin.ui.micro

object MicroNewsDataProvider {
    
    fun getMicroNews(): List<MicroNewsItem> {
        return listOf(
            MicroNewsItem(
                id = "1",
                content = "今天天气真好，适合出去走走。大家周末都有什么计划呢？",
                imageList = listOf("img1.jpg", "img2.jpg"),
                authorName = "阳光明媚",
                authorAvatar = "avatar1.jpg",
                publishTime = "2小时前",
                likeCount = 128,
                commentCount = 36,
                shareCount = 12
            ),
            MicroNewsItem(
                id = "2",
                content = "刚刚发布了最新的技术文章，详细介绍了Android开发中的性能优化技巧，欢迎大家阅读！",
                imageList = listOf("article1.jpg"),
                authorName = "技术达人",
                authorAvatar = "avatar2.jpg",
                publishTime = "4小时前",
                likeCount = 256,
                commentCount = 48,
                shareCount = 75
            ),
            MicroNewsItem(
                id = "3",
                content = "分享一下今天做的美食，简单又好吃，适合工作日快速搞定！#美食分享# #家常菜#",
                imageList = listOf("food1.jpg", "food2.jpg", "food3.jpg"),
                authorName = "美食家",
                authorAvatar = "avatar3.jpg",
                publishTime = "昨天 18:30",
                likeCount = 512,
                commentCount = 128,
                shareCount = 96
            ),
            MicroNewsItem(
                id = "4",
                content = "最近在学习摄影，这是我的第一组作品，希望大家喜欢并给点建议。",
                imageList = listOf("photo1.jpg", "photo2.jpg", "photo3.jpg", "photo4.jpg"),
                authorName = "摄影初学者",
                authorAvatar = "avatar4.jpg",
                publishTime = "2天前",
                likeCount = 342,
                commentCount = 89,
                shareCount = 45
            ),
            MicroNewsItem(
                id = "5",
                content = "终于完成了这个月的项目目标，团队合作真的很重要！感谢所有小伙伴的努力！",
                imageList = emptyList(),
                authorName = "项目经理",
                authorAvatar = "avatar5.jpg",
                publishTime = "3天前",
                likeCount = 189,
                commentCount = 23,
                shareCount = 17
            )
        )
    }
}