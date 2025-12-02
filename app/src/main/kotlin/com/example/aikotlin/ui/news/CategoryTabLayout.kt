package com.example.aikotlin.ui.news

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.aikotlin.R
import com.example.aikotlin.model.NewsCategory

class CategoryTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val tabContainer: LinearLayout
    private var selectedCategoryId: String? = null
    private var onCategorySelectedListener: OnCategorySelectedListener? = null
    
    // 默认分类列表
    private val defaultCategories = listOf(
        NewsCategory("all", "推荐", android.R.drawable.ic_menu_mylocation),
        NewsCategory("tech", "科技", android.R.drawable.ic_menu_manage),
        NewsCategory("finance", "财经", android.R.drawable.ic_menu_agenda),
        NewsCategory("sports", "体育", android.R.drawable.ic_menu_sort_by_size),
        NewsCategory("entertainment", "娱乐", android.R.drawable.ic_menu_gallery),
        NewsCategory("health", "健康", android.R.drawable.ic_menu_my_calendar),
        NewsCategory("education", "教育", android.R.drawable.ic_menu_edit),
        NewsCategory("auto", "汽车", android.R.drawable.ic_menu_zoom),
        NewsCategory("travel", "旅游", android.R.drawable.ic_menu_directions)
    )

    init {
        val inflater = LayoutInflater.from(context)
        val rootView = inflater.inflate(R.layout.category_tab_layout, this, false)
        addView(rootView)
        
        tabContainer = rootView.findViewById(R.id.tab_container)
        setupTabs(defaultCategories)
    }

    private fun setupTabs(categories: List<NewsCategory>) {
        tabContainer.removeAllViews()
        
        categories.forEachIndexed { index, category ->
            val tabView = createTabView(category)
            tabContainer.addView(tabView)
            
            // 默认选中第一个分类
            if (index == 0) {
                selectTab(category.id)
            }
        }
    }

    private fun createTabView(category: NewsCategory): View {
        val tabView = LayoutInflater.from(context).inflate(R.layout.category_tab_item, tabContainer, false)
        val tabText = tabView.findViewById<TextView>(R.id.tab_text)
        
        tabText.text = category.name
        
        tabView.setOnClickListener {
            selectTab(category.id)
            onCategorySelectedListener?.onCategorySelected(category.id, category.name)
        }
        
        return tabView
    }

    fun selectTab(categoryId: String) {
        selectedCategoryId = categoryId
        
        // 更新所有tab的样式
        for (i in 0 until tabContainer.childCount) {
            val tabView = tabContainer.getChildAt(i)
            val tabText = tabView.findViewById<TextView>(R.id.tab_text)
            val indicatorView = tabView.findViewById<View>(R.id.indicator_view)
            
            val isSelected = i == findTabIndexById(categoryId)
            
            if (isSelected) {
                tabText.setTextColor(context.getColor(R.color.colorPrimary))
                tabText.textSize = 18f
                indicatorView.visibility = View.VISIBLE
                
                // 滚动到选中的tab
                tabView.post {
                    val tabLeft = tabView.left
                    val tabRight = tabView.right
                    val screenWidth = width
                    
                    if (tabLeft < scrollX || tabRight > scrollX + screenWidth) {
                        smoothScrollTo(tabLeft - screenWidth / 4, 0)
                    }
                }
            } else {
                tabText.setTextColor(context.getColor(android.R.color.darker_gray))
                tabText.textSize = 16f
                indicatorView.visibility = View.GONE
            }
        }
    }

    private fun findTabIndexById(categoryId: String): Int {
        defaultCategories.forEachIndexed { index, category ->
            if (category.id == categoryId) {
                return index
            }
        }
        return 0 // 默认返回第一个
    }

    fun setOnCategorySelectedListener(listener: OnCategorySelectedListener) {
        this.onCategorySelectedListener = listener
    }

    interface OnCategorySelectedListener {
        fun onCategorySelected(categoryId: String, categoryName: String)
    }
}