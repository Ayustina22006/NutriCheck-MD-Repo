package com.example.nutricheck.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nutricheck.data.response.ArticleDataItem
import com.example.nutricheck.databinding.ItemArticleBinding
import com.squareup.picasso.Picasso

import android.content.Intent
import android.net.Uri
import com.example.nutricheck.data.entity.ArticleEntity

class ArticleAdapter : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private val articleList = mutableListOf<ArticleDataItem?>()

    private fun convertToArticleDataItem(articleEntity: ArticleEntity): ArticleDataItem {
        return ArticleDataItem(
            id = articleEntity.id,
            title = articleEntity.title,
            description = articleEntity.description,
            image = articleEntity.image,
            url = articleEntity.url,
            categories = articleEntity.categories.split(",")
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(articles: List<ArticleEntity>) {
        articleList.clear()
        articleList.addAll(articles.map { convertToArticleDataItem(it) })
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articleList[position])
    }

    override fun getItemCount(): Int = articleList.size

    class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleDataItem?) {
            binding.tvArticleTitle.text = article?.title
            binding.tvArticleDescription.text = article?.description
            Picasso.get().load(article?.image).into(binding.ivArticleImage)

            binding.tvArticleCategory1.visibility = View.GONE
            binding.tvArticleCategory2.visibility = View.GONE

            val categories = article?.categories
            when {
                categories?.size == 1 -> {
                    binding.tvArticleCategory1.text = categories[0]
                    binding.tvArticleCategory1.visibility = View.VISIBLE
                }
                (categories?.size ?: 0) >= 2 -> {
                    binding.tvArticleCategory1.text = categories!![0]
                    binding.tvArticleCategory2.text = categories[1]
                    binding.tvArticleCategory1.visibility = View.VISIBLE
                    binding.tvArticleCategory2.visibility = View.VISIBLE
                }
            }

            binding.root.setOnClickListener {
                article?.url?.let { url ->
                    val context = binding.root.context
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            }
        }
    }
}

