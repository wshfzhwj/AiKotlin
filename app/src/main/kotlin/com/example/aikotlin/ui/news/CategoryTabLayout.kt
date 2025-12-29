package com.example.aikotlin.ui.news

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.aikotlin.R
import com.example.aikotlin.model.Category

class CategoryTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val tabContainer: LinearLayout
    private var selectedCategoryId: String? = null
    private var onCategorySelectedListener: OnCategorySelectedListener? = null
    private var categories: List<Category> = emptyList()

    init {
        val inflater = LayoutInflater.from(context)
        val rootView = inflater.inflate(R.layout.category_tab_layout, this, false)
        addView(rootView)
        tabContainer = rootView.findViewById(R.id.tab_container)
    }

    fun setCategories(categories: List<Category>) {
        this.categories = categories
        setupTabs(categories)
    }

    private fun setupTabs(categories: List<Category>) {
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

    private fun createTabView(category: Category): View {
        val tabView = LayoutInflater.from(context).inflate(R.layout.category_tab_item, tabContainer, false)
        val tabText = tabView.findViewById<TextView>(R.id.tab_text)

        tabText.text = category.name

        tabView.setOnClickListener {
            if (selectedCategoryId != category.id) {
                selectTab(category.id)
                onCategorySelectedListener?.onCategorySelected(category.id, category.name)
            }
        }

        return tabView
    }

    fun selectTab(categoryId: String) {
        selectedCategoryId = categoryId
        val selectedIndex = findTabIndexById(categoryId)

        for (i in 0 until tabContainer.childCount) {
            val tabView = tabContainer.getChildAt(i)
            val tabText = tabView.findViewById<TextView>(R.id.tab_text)
            val indicatorView = tabView.findViewById<View>(R.id.indicator_view)

            val isSelected = i == selectedIndex

            if (isSelected) {
                tabText.setTextColor(context.getColor(R.color.colorPrimary))
                tabText.textSize = 18f
                indicatorView.visibility = View.VISIBLE

                tabView.post {
                    val scrollX = this.scrollX
                    val tabLeft = tabView.left
                    val tabRight = tabView.right
                    val myWidth = this.width

                    if (tabLeft < scrollX || tabRight > scrollX + myWidth) {
                        val scrollTo = tabLeft - (myWidth / 2) + (tabView.width / 2)
                        smoothScrollTo(scrollTo, 0)
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
        return categories.indexOfFirst { it.id == categoryId }.takeIf { it != -1 } ?: 0
    }

    fun setOnCategorySelectedListener(listener: OnCategorySelectedListener) {
        this.onCategorySelectedListener = listener
    }

    interface OnCategorySelectedListener {
        fun onCategorySelected(categoryId: String, categoryName: String)
    }
}
