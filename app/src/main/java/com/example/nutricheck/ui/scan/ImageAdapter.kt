package com.example.nutricheck.ui.scan

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.nutricheck.R
import com.example.nutricheck.databinding.ItemImageBinding
import java.io.File

class ImageAdapter(private var foodItems: List<CapturedFoodItem>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newFoodItems: List<CapturedFoodItem>) {
        Log.d("ImageAdapter", "Updating data with size: ${newFoodItems.size}")
        foodItems = newFoodItems
        notifyDataSetChanged()
        Log.d("ImageAdapter", "notifyDataSetChanged called. Adapter itemCount: ${itemCount}")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val foodItem = foodItems[position]
        Log.d("ImageAdapter", "Binding item at position $position: ${foodItem.imagePath}")
        holder.bind(foodItem)
    }

    override fun getItemCount(): Int {
        Log.d("ImageAdapter", "Item count: ${foodItems.size}")
        return foodItems.size
    }

    inner class ImageViewHolder(private val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CapturedFoodItem) {
            val file = File(Uri.parse(item.imagePath).path ?: "")
            Glide.with(binding.root.context)
                .load(file)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("ImageAdapter", "Glide load failed", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("ImageAdapter", "Glide load success")
                        return false
                    }
                })
                .error(R.drawable.placeholder)
                .into(binding.foodImageView)
        }
    }
}