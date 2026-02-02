package com.example.aikotlin.data

import com.example.aikotlin.model.*

object MallDataProvider {
    fun getMallHomeData(): MallHomeData {
        return MallHomeData(
            banners = listOf(
                BannerItem("1", "https://img.freepik.com/free-vector/big-sale-banner-template-design_52683-71158.jpg"),
                BannerItem("2", "https://img.freepik.com/free-vector/shopping-day-sale-banner-design_23-2148679040.jpg")
            ),
            categories = listOf(
                MallCategory("1", "Supermart", "https://cdn-icons-png.flaticon.com/512/3737/3737372.png", "#FF6B6B"),
                MallCategory("2", "Electronics", "https://cdn-icons-png.flaticon.com/512/3659/3659899.png", "#4ECDC4"),
                MallCategory("3", "Fashion", "https://cdn-icons-png.flaticon.com/512/3050/3050239.png", "#45B7D1"),
                MallCategory("4", "Fresh", "https://cdn-icons-png.flaticon.com/512/2909/2909761.png", "#96CEB4"),
                MallCategory("5", "Topup", "https://cdn-icons-png.flaticon.com/512/2933/2933116.png", "#FFEEAD"),
                MallCategory("6", "Travel", "https://cdn-icons-png.flaticon.com/512/201/201623.png", "#D4A5A5"),
                MallCategory("7", "Beauty", "https://cdn-icons-png.flaticon.com/512/194/194931.png", "#9B59B6"),
                MallCategory("8", "Home", "https://cdn-icons-png.flaticon.com/512/619/619034.png", "#3498DB"),
                MallCategory("9", "Pets", "https://cdn-icons-png.flaticon.com/512/616/616408.png", "#E67E22"),
                MallCategory("10", "More", "https://cdn-icons-png.flaticon.com/512/512/512142.png", "#2ECC71")
            ),
            seckillProducts = listOf(
                SeckillProduct("s1", "Product 1", "https://picsum.photos/seed/s1/200/200", 9.09, 19.99),
                SeckillProduct("s2", "Product 2", "https://picsum.photos/seed/s2/200/200", 9.19, 18.99),
                SeckillProduct("s3", "Product 3", "https://picsum.photos/seed/s3/200/200", 9.29, 17.99),
                SeckillProduct("s4", "Product 4", "https://picsum.photos/seed/s4/200/200", 9.39, 16.99)
            ),
            feedProducts = listOf(
                MallProduct("p1", "WH-1000XM5", "Sony", "https://picsum.photos/seed/p1/400/600", 299.0, listOf("Free Ship")),
                MallProduct("p2", "90 High", "Nike Air Max", "https://picsum.photos/seed/p2/400/800", 120.0, listOf("Free Ship")),
                MallProduct("p3", "iPhone 15 Pro", "Apple", "https://picsum.photos/seed/p3/400/500", 999.0, listOf("Best Seller")),
                MallProduct("p4", "Galaxy S24 Ultra", "Samsung", "https://picsum.photos/seed/p4/400/700", 1199.0, listOf("New Arrival")),
                MallProduct("p5", "Bose QuietComfort", "Bose", "https://picsum.photos/seed/p5/400/400", 349.0, listOf("Free Ship")),
                MallProduct("p6", "Dyson V15", "Dyson", "https://picsum.photos/seed/p6/400/900", 749.0, listOf("High Tech"))
            )
        )
    }
}
