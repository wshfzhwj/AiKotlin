package com.example.aikotlin.data

import com.example.aikotlin.model.Category
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.utils.CryptoUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

object FakeNewsDataProvider {

    private val gson = Gson()
    private var cachedArticles: List<NewsArticle>? = null

    // 将文章列表定义为JSON字符串，模拟网络返回的数据
    private val articlesJson = CryptoUtils.encrypt("""
    [
        {
            "id": "tech_1",
            "title": "今日头条：科技巨头发布全新AI模型，性能超越行业标准",
            "content": "该公司今日发布了最新的人工智能模型，在多项基准测试中表现优异，为行业带来新的突破。据报道，这一模型在自然语言处理、图像识别等领域均有显著提升。",
            "source": "科技日报",
            "author": "记者 张三",
            "publishedAt": "2024-01-15T08:30:00Z",
            "url": "https://example.com/news/tech_1",
            "urlToImage": "https://example.com/images/tech_news1.jpg",
            "category": "technology"
        },
        {
            "id": "sports_1",
            "title": "体育快讯：国足世预赛取得开门红，2:0战胜对手",
            "content": "在昨晚进行的世界杯预选赛中，中国国家男子足球队以2:0的比分战胜对手，取得了预选赛的开门红。两名球员分别在上半场和下半场各入一球。",
            "source": "体育周报",
            "author": "记者 李四",
            "publishedAt": "2024-01-15T06:45:00Z",
            "url": "https://example.com/news/sports_1",
            "urlToImage": "https://example.com/images/sports_news1.jpg",
            "category": "sports"
        },
        {
            "id": "business_1",
            "title": "财经分析：A股市场持续走强，三大指数全线上涨",
            "content": "今日A股市场表现强势，三大指数全线上涨。截至收盘，上证指数上涨2.1%，深证成指上涨2.5%，创业板指上涨3.2%。科技、金融板块领涨。",
            "source": "财经网",
            "author": "分析师 王五",
            "publishedAt": "2024-01-15T05:10:00Z",
            "url": "https://example.com/news/business_1",
            "urlToImage": "https://example.com/images/finance_news1.jpg",
            "category": "business"
        },
        {
            "id": "education_1",
            "title": "教育改革：新政策出台，将加强学生体育锻炼",
            "content": "教育部今日发布新政策，要求各学校加强学生体育锻炼，确保学生每天至少有一小时的体育活动时间。同时，将把体育成绩纳入中考评价体系。",
            "source": "教育时报",
            "author": "记者 赵六",
            "publishedAt": "2024-01-14T18:20:00Z",
            "url": "https://example.com/news/education_1",
            "urlToImage": "https://example.com/images/education_news1.jpg",
            "category": "education"
        },
        {
            "id": "health_1",
            "title": "健康科普：专家提醒冬季预防感冒的5个小技巧",
            "content": "随着气温降低，感冒进入高发季节。专家建议大家注意保暖、勤洗手、保持室内通风、适当运动增强免疫力，并保持充足的睡眠。",
            "source": "健康时报",
            "author": "医生 孙七",
            "publishedAt": "2024-01-14T16:45:00Z",
            "url": "https://example.com/news/health_1",
            "urlToImage": "https://example.com/images/health_news1.jpg",
            "category": "health"
        },
        {
            "id": "entertainment_1",
            "title": "娱乐资讯：年度电影节揭幕，多部国产大片参展",
            "content": "第X届国际电影节今日在上海开幕，共有来自全球40多个国家和地区的300多部电影参展。其中，国产大片《XXX》备受关注，有望冲击奖项。",
            "source": "娱乐周刊",
            "author": "记者 周八",
            "publishedAt": "2024-01-14T14:10:00Z",
            "url": "https://example.com/news/entertainment_1",
            "urlToImage": "https://example.com/images/entertainment_news1.jpg",
            "category": "entertainment"
        },
        {
            "id": "travel_1",
            "title": "旅游推荐：冬季最适合旅游的5个国内目的地",
            "content": "冬季来临，想出门旅游却不知道去哪？本文推荐了五个适合冬季旅游的国内目的地，包括哈尔滨冰雪大世界、三亚阳光海岸、丽江古城等。",
            "source": "旅游杂志",
            "author": "旅行达人 吴九",
            "publishedAt": "2024-01-14T12:30:00Z",
            "url": "https://example.com/news/travel_1",
            "urlToImage": "https://example.com/images/travel_news1.jpg",
            "category": "travel"
        },
        {
            "id": "auto_1",
            "title": "汽车资讯：新能源汽车销量持续增长，市场份额创新高",
            "content": "最新数据显示，今年12月新能源汽车销量同比增长50%，市场份额达到25%，创历史新高。多家车企表示将加大新能源车型的研发投入。",
            "source": "汽车之家",
            "author": "汽车编辑 郑十",
            "publishedAt": "2024-01-14T10:50:00Z",
            "url": "https://example.com/news/auto_1",
            "urlToImage": "https://example.com/images/auto_news1.jpg",
            "category": "auto"
        },
        {
            "id": "general_1",
            "title": "社会热点：多地推出暖心举措保障春节期间民生供应",
            "content": "为确保春节期间的市场供应和价格稳定，多地政府推出暖心举措，包括发放消费券、增加保供企业库存、延长商超营业时间等。",
            "source": "人民日报",
            "author": "记者 钱十一",
            "publishedAt": "2024-01-14T09:15:00Z",
            "url": "https://example.com/news/general_1",
            "urlToImage": "https://example.com/images/general_news1.jpg",
            "category": "general"
        },
        {
            "id": "science_1",
            "title": "科学发现：深空望远镜捕捉到罕见星际现象",
            "content": "最新的观测数据显示，科研人员首次捕捉到某类罕见的星际磁暴现象，这将有助于人类更好地理解宇宙的形成和演化。",
            "source": "科学世界",
            "author": "研究员 周十二",
            "publishedAt": "2024-01-14T07:40:00Z",
            "url": "https://example.com/news/science_1",
            "urlToImage": "https://example.com/images/science_news1.jpg",
            "category": "science"
        },
        {
            "id": "business_2",
            "title": "市场观察：全球芯片供应趋于稳定，价格回落",
            "content": "随着产能的逐步恢复以及新工厂的投产，全球芯片供应紧张局面正在缓解，主要芯片品类的价格较去年高点回落了15%。",
            "source": "全球财经",
            "author": "分析师 冯十三",
            "publishedAt": "2024-01-13T19:05:00Z",
            "url": "https://example.com/news/business_2",
            "urlToImage": "https://example.com/images/business_news2.jpg",
            "category": "business"
        },
        {
            "id": "technology_2",
            "title": "行业观察：云计算厂商发布边缘AI一体机",
            "content": "为了满足工业互联网场景的高实时性需求，某云计算厂商推出边缘AI一体机，可在毫秒级内完成推理并回传结果。",
            "source": "硅谷周刊",
            "author": "特约作者 韩十四",
            "publishedAt": "2024-01-13T11:25:00Z",
            "url": "https://example.com/news/technology_2",
            "urlToImage": "https://example.com/images/tech_news2.jpg",
            "category": "technology"
        }
    ]
    """.trimIndent())

    private val categoryAliases = mapOf(
        "all" to "all",
        "general" to "general",
        "tech" to "technology",
        "technology" to "technology",
        "business" to "business",
        "finance" to "business",
        "sports" to "sports",
        "entertainment" to "entertainment",
        "health" to "health",
        "science" to "science",
        "education" to "education",
        "auto" to "auto",
        "travel" to "travel"
    )

    private fun getParsedArticles(): List<NewsArticle> {
        if (cachedArticles != null) {
            return cachedArticles!!
        }
        val articleListType = object : TypeToken<List<NewsArticle>>() {}.type
        val articles: List<NewsArticle> = gson.fromJson(CryptoUtils.decrypt(articlesJson), articleListType)
        cachedArticles = articles
        return articles
    }

    fun getAllArticles(): List<NewsArticle> = getParsedArticles()

    fun getArticlesByCategory(category: String?): List<NewsArticle> {
        val allArticles = getParsedArticles()
        val normalized = normalizeCategory(category)
        if (normalized == null || normalized == "all") {
            return allArticles
        }
        return allArticles.filter { it.category == normalized }
    }

    fun searchArticles(query: String): List<NewsArticle> {
        val allArticles = getParsedArticles()
        if (query.isBlank()) return emptyList()
        val keyword = query.trim().lowercase(Locale.getDefault())
        return allArticles.filter {
            it.title.lowercase(Locale.getDefault()).contains(keyword) ||
                    it.content.lowercase(Locale.getDefault()).contains(keyword) ||
                    it.source.lowercase(Locale.getDefault()).contains(keyword)
        }
    }

    private fun normalizeCategory(rawCategory: String?): String? {
        if (rawCategory == null) return null
        val lower = rawCategory.lowercase(Locale.getDefault())
        return categoryAliases[lower] ?: lower
    }

    private val newsCategories = listOf(
        Category("general", "推荐"),
        Category("tech", "科技"),
        Category("finance", "财经"),
        Category("sports", "体育"),
        Category("entertainment", "娱乐"),
        Category("health", "健康"),
        Category("education", "教育"),
        Category("auto", "汽车"),
        Category("travel", "旅游")
    )

    fun getCategories(): List<Category> = newsCategories
}
