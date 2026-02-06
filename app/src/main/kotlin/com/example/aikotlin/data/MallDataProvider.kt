package com.example.aikotlin.data

import com.example.aikotlin.model.*

object MallDataProvider {
    fun getMallHomeData(page: Int = 1, pageSize: Int = 10): MallHomeData {
        val banners = if (page == 1) listOf(
            BannerItem("1", "https://img.freepik.com/free-vector/big-sale-banner-template-design_52683-71158.jpg"),
            BannerItem("2", "https://img.freepik.com/free-vector/shopping-day-sale-banner-design_23-2148679040.jpg")
        ) else emptyList()

        val categories = if (page == 1) listOf(
            MallCategory("1", "超市", "https://cdn-icons-png.flaticon.com/512/3737/3737372.png", "#FF6B6B"),
            MallCategory("2", "数码电器", "https://cdn-icons-png.flaticon.com/512/3659/3659899.png", "#4ECDC4"),
            MallCategory("3", "服饰时尚", "https://cdn-icons-png.flaticon.com/512/3050/3050239.png", "#45B7D1"),
            MallCategory("4", "生鲜", "https://cdn-icons-png.flaticon.com/512/2909/2909761.png", "#96CEB4"),
            MallCategory("5", "充值缴费", "https://cdn-icons-png.flaticon.com/512/2933/2933116.png", "#FFEEAD"),
            MallCategory("6", "旅行", "https://cdn-icons-png.flaticon.com/512/201/201623.png", "#D4A5A5"),
            MallCategory("7", "美妆护肤", "https://cdn-icons-png.flaticon.com/512/194/194931.png", "#9B59B6"),
            MallCategory("8", "家居", "https://cdn-icons-png.flaticon.com/512/619/619034.png", "#3498DB"),
            MallCategory("9", "宠物", "https://cdn-icons-png.flaticon.com/512/616/616408.png", "#E67E22"),
            MallCategory("10", "更多", "https://cdn-icons-png.flaticon.com/512/512/512142.png", "#2ECC71")
        ) else emptyList()

        val seckillProducts = if (page == 1) listOf(
            SeckillProduct("s1", "特价商品 1", "https://picsum.photos/seed/s1/200/200", 9.09, 19.99),
            SeckillProduct("s2", "特价商品 2", "https://picsum.photos/seed/s2/200/200", 9.19, 18.99),
            SeckillProduct("s3", "特价商品 3", "https://picsum.photos/seed/s3/200/200", 9.29, 17.99),
            SeckillProduct("s4", "特价商品 4", "https://picsum.photos/seed/s4/200/200", 9.39, 16.99)
        ) else emptyList()

        val feedProducts = generateFeedProducts(page, pageSize)

        return MallHomeData(
            banners = banners,
            categories = categories,
            seckillProducts = seckillProducts,
            feedProducts = feedProducts
        )
    }

    private fun generateFeedProducts(page: Int, pageSize: Int): List<MallProduct> {
        val products = mutableListOf<MallProduct>()
        val startId = (page - 1) * pageSize
        for (i in 1..pageSize) {
            val id = startId + i
            products.add(
                MallProduct(
                    id = "p$id",
                    name = "示例商品名称 $id",
                    brand = "品牌 ${id % 5}",
                    imageUrl = "https://picsum.photos/seed/p$id/400/${if (id % 2 == 0) 600 else 400}",
                    price = 10.0 * id,
                    tags = if (id % 2 == 0) listOf("包邮") else listOf("热销")
                )
            )
        }
        return products
    }
}
