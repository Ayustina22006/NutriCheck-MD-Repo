package com.example.nutricheck.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nutricheck.data.response.ArticleDataItem
import com.example.nutricheck.databinding.ItemArticleBinding
import com.squareup.picasso.Picasso

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private val articleList = mutableListOf<ArticleDataItem?>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(articles: List<ArticleDataItem?>?) {
        articleList.clear()
        if (articles != null) {
            articleList.addAll(articles)
        }
        notifyDataSetChanged()
    }

    // Create view holder with binding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    // Bind data to view holder
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articleList[position])
    }

    override fun getItemCount(): Int = articleList.size

    // ViewHolder with binding
    class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleDataItem?) {
            binding.tvArticleTitle.text = article?.title
            binding.tvArticleDescription.text = article?.description
            Picasso.get().load(article?.image).into(binding.ivArticleImage)

            // Display categories if available
            val categories = article?.category
            if (categories != null && categories.isNotEmpty()) {
                // Show the first category in tvArticleCategory1
                binding.tvArticleCategory1.text = categories.getOrNull(0) ?: ""
                binding.tvArticleCategory1.visibility = if (categories.isNotEmpty()) View.VISIBLE else View.GONE

                // Show the second category in tvArticleCategory2
                binding.tvArticleCategory2.text = categories.getOrNull(1) ?: ""
                binding.tvArticleCategory2.visibility = if (categories.size > 1) View.VISIBLE else View.GONE
            }
        }
    }
}

