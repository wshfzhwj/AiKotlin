package com.example.aikotlin.model

data class BannerItem(
    val id: String,
    val imageUrl: String,
    val linkUrl: String? = null
)

data class MallCategory(
    val id: String,
    val name: String,
    val iconUrl: String,
    val backgroundColor: String? = null
)

data class SeckillProduct(
    val id: String,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val originalPrice: Double
)

data class MallProduct(
    val id: String,
    val name: String,
    val brand: String,
    val imageUrl: String,
    val price: Double,
    val tags: List<String> = emptyList()
)

data class MallHomeData(
    val banners: List<BannerItem>,
    val categories: List<MallCategory>,
    val seckillProducts: List<SeckillProduct>,
    val feedProducts: List<MallProduct>
)
