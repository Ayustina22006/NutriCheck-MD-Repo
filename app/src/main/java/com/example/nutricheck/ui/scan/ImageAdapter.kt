package com.example.nutricheck.ui.scan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.nutricheck.R
import com.example.nutricheck.data.database.AppDao
import com.example.nutricheck.data.entity.CapturedFoodItem
import com.example.nutricheck.databinding.DialogImageBinding
import com.example.nutricheck.databinding.ItemImageBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class ImageAdapter(
    private var foodItems: List<CapturedFoodItem>,
    private val appDao: AppDao
) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

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
            val context = binding.root.context
            val imageName = item.foodName?.let { File(it).nameWithoutExtension }
            val uri = Uri.parse(item.imagePath)

            binding.foodNameTextView.text = imageName ?: "Unknown Food"

            Glide.with(context)
                .load(uri)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("ImageAdapter", "Failed to load image: $uri", e)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d("ImageAdapter", "Successfully loaded image: $uri")
                        return false
                    }
                })
                .error(R.drawable.placeholder) // Placeholder jika gagal memuat gambar
                .into(binding.foodImageView)

            // Click listener untuk menampilkan dialog
            binding.foodImageView.setOnClickListener {
                showImageInDialog(context, uri, item)
            }
        }

        private fun showImageInDialog(context: Context, imageUri: Uri, item: CapturedFoodItem) {
            val binding = DialogImageBinding.inflate(LayoutInflater.from(context))
            val dialog = Dialog(context)
            dialog.setContentView(binding.root)

            Glide.with(context)
                .load(imageUri)
                .error(R.drawable.placeholder)
                .into(binding.dialogImageView)

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            binding.deleteButton.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    appDao.delete(item)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Image deleted successfully", Toast.LENGTH_SHORT).show()
                        (foodItems as MutableList).remove(item)
                        notifyDataSetChanged()
                        dialog.dismiss()
                    }
                }
            }

            dialog.show()
        }

    }
}