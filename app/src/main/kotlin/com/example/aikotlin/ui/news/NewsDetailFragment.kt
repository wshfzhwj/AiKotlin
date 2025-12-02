package com.example.aikotlin.ui.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.aikotlin.R
import com.example.aikotlin.base.BaseFragment
import com.example.aikotlin.databinding.FragmentNewsDetailBinding
import com.example.aikotlin.model.NewsArticle
import com.example.aikotlin.viewmodel.NewsDetailViewModel
import com.example.aikotlin.viewmodel.NewsViewModel
import java.text.SimpleDateFormat
import kotlin.getValue

class NewsDetailFragment : BaseFragment<FragmentNewsDetailBinding, NewsDetailViewModel>() {
    override val viewModel: NewsDetailViewModel by viewModels()
    private val args: NewsDetailFragmentArgs by navArgs()
    private lateinit var article: NewsArticle
    private lateinit var titleTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var sourceTextView: TextView
    private lateinit var authorTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var newsImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 获取传入的新闻文章数据
        article = args.article
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 初始化视图
        titleTextView = view.findViewById(R.id.detail_title)
        contentTextView = view.findViewById(R.id.detail_content)
        sourceTextView = view.findViewById(R.id.detail_source)
        authorTextView = view.findViewById(R.id.detail_author)
        dateTextView = view.findViewById(R.id.detail_date)
        newsImage = view.findViewById(R.id.detail_image)

        // 绑定数据
        bindArticleData()
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewsDetailBinding {
        return FragmentNewsDetailBinding.inflate(inflater, container, false)
    }

    private fun bindArticleData() {
        titleTextView.text = article.title
        contentTextView.text = article.content
        sourceTextView.text = "来源: ${article.source}"
        authorTextView.text = "作者: ${article.author}"
        dateTextView.text = formatDate(article.publishedAt)

        // 使用彩色默认图片
        newsImage.setImageResource(R.drawable.ic_placeholder_news_colorful)
    }

    private fun formatDate(date: java.util.Date): String {
        val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss")
        return sdf.format(date)
    }
}