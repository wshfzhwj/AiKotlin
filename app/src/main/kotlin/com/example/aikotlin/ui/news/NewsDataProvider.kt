package com.example.aikotlin.ui.news

import com.example.aikotlin.data.FakeNewsDataProvider
import com.example.aikotlin.model.NewsArticle

object NewsDataProvider {
    
    fun getNewsArticles(): List<NewsArticle> {
        return FakeNewsDataProvider.getAllArticles()
    }
    
    fun getNewsByCategory(category: String): List<NewsArticle> {
        return FakeNewsDataProvider.getArticlesByCategory(category)
    }
}