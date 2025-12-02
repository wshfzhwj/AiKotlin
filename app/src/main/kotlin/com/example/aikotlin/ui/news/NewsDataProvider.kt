package com.example.aikotlin.ui.news

import com.example.aikotlin.data.fake.FakeNewsDataStore
import com.example.aikotlin.model.NewsArticle

object NewsDataProvider {
    
    fun getNewsArticles(): List<NewsArticle> {
        return FakeNewsDataStore.getAllArticles()
    }
    
    fun getNewsByCategory(category: String): List<NewsArticle> {
        return FakeNewsDataStore.getArticlesByCategory(category)
    }
}