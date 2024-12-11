package com.example.nutricheck.ui.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nutricheck.R
import com.example.nutricheck.data.response.MealsDetailsHistoryItem
import com.example.nutricheck.databinding.ItemDetailBinding

class HistoryDetailAdapter(
    private val onItemClick: (MealsDetailsHistoryItem) -> Unit
) : ListAdapter<MealsDetailsHistoryItem, HistoryDetailAdapter.NutritionViewHolder>(MealDetailDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionViewHolder {
        val binding = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NutritionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NutritionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NutritionViewHolder(private val binding: ItemDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(mealDetail: MealsDetailsHistoryItem) {
            binding.apply {
                // Set nama makanan dari foodName
                textFoodName.text = mealDetail.foodName ?: "Unknown Food"

                // Set serving size dan calories
                textFoodDetail.text = "${mealDetail.servingSize ?: 0} g | ${mealDetail.calories ?: 0} kkal"

                // Temukan nutrisi tertinggi
                val nutritions = mealDetail.nutritions
                val highestNutrition = nutritions?.toMap()?.maxByOrNull { it.value }

                // Set text kategori sebagai nutrisi tertinggi
                textFoodCategory.text = highestNutrition?.key?: "No Nutrition"

                Glide.with(imageFood.context)
                    .load(R.drawable.placeholder_2)
                    .placeholder(R.drawable.placeholder_2)
                    .into(imageFood)
                root.setOnClickListener {
                    onItemClick(mealDetail) // Panggil callback saat item ditekan
                }
            }

        }
    }

    class MealDetailDiffCallback : DiffUtil.ItemCallback<MealsDetailsHistoryItem>() {
        override fun areItemsTheSame(oldItem: MealsDetailsHistoryItem, newItem: MealsDetailsHistoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MealsDetailsHistoryItem, newItem: MealsDetailsHistoryItem): Boolean {
            return oldItem == newItem
        }
    }
}



