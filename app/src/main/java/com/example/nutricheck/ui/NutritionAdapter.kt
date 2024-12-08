package com.example.nutricheck.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nutricheck.R
import com.example.nutricheck.data.response.NutritionItem

class NutritionAdapter : RecyclerView.Adapter<NutritionAdapter.NutritionViewHolder>() {

    private var nutritionList: List<NutritionItem> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setNutritionList(newList: List<NutritionItem>) {
        nutritionList = newList
        notifyDataSetChanged()
    }

    class NutritionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvNutrientName: TextView = view.findViewById(R.id.tvNutrientName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nutrition, parent, false)
        return NutritionViewHolder(view)
    }

    override fun onBindViewHolder(holder: NutritionViewHolder, position: Int) {
        val nutrition = nutritionList[position]
        holder.tvAmount.text = nutrition.amount
        holder.tvNutrientName.text = nutrition.name
    }

    override fun getItemCount(): Int = nutritionList.size
}
