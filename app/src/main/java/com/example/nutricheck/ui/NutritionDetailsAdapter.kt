package com.example.nutricheck.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.nutricheck.R
import com.example.nutricheck.data.response.MealDetail
import com.example.nutricheck.databinding.ItemDetailBinding

class NutritionDetailsAdapter(
    private val onItemClick: (MealDetail) -> Unit
) : ListAdapter<MealDetail, NutritionDetailsAdapter.NutritionViewHolder>(MealDetailDiffCallback()) {

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
        fun bind(mealDetail: MealDetail) {
            binding.apply {
                // Set food name
                textFoodName.text = mealDetail.foodName ?: "Unknown Food"

                // Set serving size and calories
                textFoodDetail.text = "${mealDetail.servingSize ?: 0} g | ${mealDetail.calories ?: 0} kcal"

                // Determine the highest nutrition value
                val highestNutrition = mealDetail.nutritionDetails?.let { nutritions ->
                    nutritions.toMap().maxByOrNull { it.value }
                }

                // Display the highest category
                textFoodCategory.text = highestNutrition?.key ?: "No Details"

                // Load image with Glide
                Glide.with(imageFood.context)
                    .load(mealDetail)
                    .placeholder(R.drawable.placeholder)
                    .into(imageFood)

                root.setOnClickListener {
                    onItemClick(mealDetail) // Panggil callback saat item ditekan
                }
            }
        }

    }

    class MealDetailDiffCallback : DiffUtil.ItemCallback<MealDetail>() {
        override fun areItemsTheSame(oldItem: MealDetail, newItem: MealDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MealDetail, newItem: MealDetail): Boolean {
            return oldItem == newItem
        }
    }
}
