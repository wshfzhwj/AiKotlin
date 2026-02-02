package com.example.aikotlin.ui.mall

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.aikotlin.databinding.*
import com.example.aikotlin.model.*

class MallAdapter : ListAdapter<MallItem, RecyclerView.ViewHolder>(MallDiffCallback()) {

    companion object {
        const val TYPE_BANNER = 0
        const val TYPE_CATEGORY_GRID = 1
        const val TYPE_SECKILL = 2
        const val TYPE_PRODUCT_CARD = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MallItem.Banner -> TYPE_BANNER
            is MallItem.CategoryGrid -> TYPE_CATEGORY_GRID
            is MallItem.Seckill -> TYPE_SECKILL
            is MallItem.ProductCard -> TYPE_PRODUCT_CARD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_BANNER -> BannerViewHolder(ItemMallBannerBinding.inflate(inflater, parent, false))
            TYPE_CATEGORY_GRID -> CategoryGridViewHolder(ItemMallCategoryGridBinding.inflate(inflater, parent, false))
            TYPE_SECKILL -> SeckillViewHolder(ItemMallSeckillBinding.inflate(inflater, parent, false))
            TYPE_PRODUCT_CARD -> ProductViewHolder(ItemMallProductCardBinding.inflate(inflater, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is BannerViewHolder -> holder.bind((item as MallItem.Banner).banners)
            is CategoryGridViewHolder -> holder.bind((item as MallItem.CategoryGrid).categories)
            is SeckillViewHolder -> holder.bind((item as MallItem.Seckill).products)
            is ProductViewHolder -> holder.bind((item as MallItem.ProductCard).product)
        }
    }

    class BannerViewHolder(private val binding: ItemMallBannerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(banners: List<BannerItem>) {
            if (banners.isNotEmpty()) {
                binding.ivBanner.load(banners[0].imageUrl)
            }
        }
    }

    class CategoryGridViewHolder(private val binding: ItemMallCategoryGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(categories: List<MallCategory>) {
            val adapter = CategoryAdapter()
            binding.rvCategories.adapter = adapter
            adapter.submitList(categories)
        }
    }

    class SeckillViewHolder(private val binding: ItemMallSeckillBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(products: List<SeckillProduct>) {
            val adapter = SeckillProductAdapter()
            binding.rvSeckillProducts.adapter = adapter
            adapter.submitList(products)
        }
    }

    class ProductViewHolder(private val binding: ItemMallProductCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: MallProduct) {
            binding.ivProduct.load(product.imageUrl)
            binding.tvBrand.text = product.brand
            binding.tvName.text = product.name
            binding.tvPrice.text = "$${product.price}"
            binding.tvTag.text = product.tags.firstOrNull() ?: ""
            binding.tvTag.visibility = if (product.tags.isNotEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
}

sealed class MallItem {
    data class Banner(val banners: List<BannerItem>) : MallItem()
    data class CategoryGrid(val categories: List<MallCategory>) : MallItem()
    data class Seckill(val products: List<SeckillProduct>) : MallItem()
    data class ProductCard(val product: MallProduct) : MallItem()
}

class MallDiffCallback : DiffUtil.ItemCallback<MallItem>() {
    override fun areItemsTheSame(oldItem: MallItem, newItem: MallItem): Boolean {
        return oldItem == newItem
    }
    override fun areContentsTheSame(oldItem: MallItem, newItem: MallItem): Boolean {
        return oldItem == newItem
    }
}

// Sub-adapters
class CategoryAdapter : ListAdapter<MallCategory, CategoryAdapter.ViewHolder>(CategoryDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMallCategoryCellBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    class ViewHolder(private val binding: ItemMallCategoryCellBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: MallCategory) {
            binding.ivCategoryIcon.load(category.iconUrl)
            binding.tvCategoryName.text = category.name
            category.backgroundColor?.let {
                binding.cardIcon.setCardBackgroundColor(Color.parseColor(it))
            }
        }
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<MallCategory>() {
    override fun areItemsTheSame(oldItem: MallCategory, newItem: MallCategory): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: MallCategory, newItem: MallCategory): Boolean = oldItem == newItem
}

class SeckillProductAdapter : ListAdapter<SeckillProduct, SeckillProductAdapter.ViewHolder>(SeckillDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMallSeckillProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    class ViewHolder(private val binding: ItemMallSeckillProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: SeckillProduct) {
            binding.ivProduct.load(product.imageUrl)
            binding.tvPrice.text = "$${product.price}"
            binding.tvOriginalPrice.text = "$${product.originalPrice}"
            binding.tvOriginalPrice.paintFlags = binding.tvOriginalPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }
}

class SeckillDiffCallback : DiffUtil.ItemCallback<SeckillProduct>() {
    override fun areItemsTheSame(oldItem: SeckillProduct, newItem: SeckillProduct): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: SeckillProduct, newItem: SeckillProduct): Boolean = oldItem == newItem
}
