package com.example.nutricheck.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nutricheck.R
import com.example.nutricheck.data.response.HistoryDataItem
import com.example.nutricheck.databinding.ItemHistoryBinding
import java.util.Locale

class HistoryAdapter(
    private val onItemClicked: (String, String) -> Unit // mealType and id
) : ListAdapter<HistoryDataItem, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HistoryViewHolder(
        private val binding: ItemHistoryBinding,
        private val onItemClicked: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(historyItem: HistoryDataItem) {
            binding.apply {
                // Set meal type
                textMealName.text = historyItem.mealType?.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                } ?: "Meal"

                // Set calories
                textMealDetail.text = "${historyItem.totalCalories ?: 0} cal"

                val calories = historyItem.totalCalories ?: 0
                when {
                    calories < 500 -> {
                        textHistoryCategory.text = itemView.context.getString(R.string.label_kurang)
                        textHistoryCategory.setBackgroundResource(R.drawable.bg_kurang) // Yellow background
                    }
                    calories in 500..700 -> {
                        textHistoryCategory.text = itemView.context.getString(R.string.label_ideal)
                        textHistoryCategory.setBackgroundResource(R.drawable.bg_ideal)
                    }
                    calories in 701..800 -> {
                        textHistoryCategory.text = itemView.context.getString(R.string.label_berlebih)
                        textHistoryCategory.setBackgroundResource(R.drawable.bg_berlebih) // Orange background
                    }
                    calories > 800 -> {
                        textHistoryCategory.text = itemView.context.getString(R.string.label_berlebihan)
                        textHistoryCategory.setBackgroundResource(R.drawable.bg_berlebihan) // Red background
                    }

                }

                // Set image based on meal type
                imageFood.setImageResource(
                    when (historyItem.mealType?.toLowerCase()) {
                        "breakfast" -> R.drawable.breakfast
                        "lunch" -> R.drawable.lunch
                        "dinner" -> R.drawable.dinner
                        else -> R.drawable.placeholder
                    }
                )
                itemView.setOnClickListener {
                    historyItem.mealType?.let { mealType ->
                        historyItem.date?.let { date ->
                            onItemClicked(mealType, date) // Pass mealType and date
                        }
                    }
                }
            }
        }
    }
}

    class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryDataItem>() {
        override fun areItemsTheSame(oldItem: HistoryDataItem, newItem: HistoryDataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryDataItem, newItem: HistoryDataItem): Boolean {
            return oldItem == newItem
        }
    }
