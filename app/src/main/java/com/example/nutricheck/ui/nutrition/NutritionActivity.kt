package com.example.nutricheck.ui.nutrition

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.data.response.MealHistoryResponse
import com.example.nutricheck.databinding.ActivityNutritionBinding
import com.example.nutricheck.ui.NutritionDetailsAdapter

class NutritionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNutritionBinding
    private lateinit var viewModel: NutritionViewModel
    private lateinit var nutritionDetailsAdapter: NutritionDetailsAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNutritionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the MealHistoryResponse passed from previous screen
        @Suppress("DEPRECATION") val mealHistoryResponse = intent.getParcelableExtra<MealHistoryResponse>("MEAL_HISTORY_RESPONSE")

        // Setup ViewModel
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[NutritionViewModel::class.java]

        // Load meal history data
        mealHistoryResponse?.let { viewModel.loadMealHistory(it) }

        // Setup RecyclerView
        nutritionDetailsAdapter = NutritionDetailsAdapter { mealDetail ->
            // Navigasi ke halaman detail
            val intent = Intent(this, NutritionDetailActivity::class.java).apply {
                putExtra("MEAL_DETAIL", mealDetail)
            }
            startActivity(intent)
        }

        binding.rvViewNutrition.apply {
            layoutManager = LinearLayoutManager(this@NutritionActivity)
            adapter = nutritionDetailsAdapter
        }

        mealHistoryResponse?.mealHistory?.mealsDetails?.let {
            nutritionDetailsAdapter.submitList(it)
        }
        // Observe data
        viewModel.mealDetails.observe(this) { mealDetails ->
            nutritionDetailsAdapter.submitList(mealDetails)
        }

        viewModel.mealHistory.observe(this) { mealHistory ->
            // Update overview card
            binding.textKalori.text = "Total Calories: ${mealHistory?.totalCalories ?: 0} kcal"
            val calories = mealHistory?.totalCalories ?: 0
            when {
                calories < 500 -> {
                    binding.textAlert.text = binding.root.context.getString(R.string.alert_kurang)
                }
                calories in 500..700 -> {
                    binding.textAlert.text = binding.root.context.getString(R.string.alert_ideal)
                }
                calories in 701..800 -> {
                    binding.textAlert.text = binding.root.context.getString(R.string.alert_berlebih)
                }
                calories > 800 -> {
                    binding.textAlert.text = binding.root.context.getString(R.string.alert_berlebihan)
                }
            }

            mealHistory?.totalNutrition?.let { nutrition ->
                binding.nutritionProtein.text = "Protein\n${viewModel.formatNutritionValue(nutrition.protein)} (g)"
                binding.nutritionCalories.text = "Calories\n${mealHistory.totalCalories} (kcal)"
                binding.nutritionDietaryFiber.text = "Dietary Fiber\n${viewModel.formatNutritionValue(nutrition.dietaryFiber)} (g)"
                binding.nutritionCalcium.text = "Calcium\n${viewModel.formatNutritionValue(nutrition.calcium)} (g)"
                binding.nutritionIron.text = "Iron\n${viewModel.formatNutritionValue(nutrition.iron)} (g)"
                binding.nutritionCarbohydrate.text = "Carbohydrate\n${viewModel.formatNutritionValue(nutrition.carbohydrate)} (g)"
                binding.nutritionVitaminA.text = "Vitamin A\n${viewModel.formatNutritionValue(nutrition.vitaminA)} (g)"
                binding.nutritionVitaminB.text = "Vitamin B\n${viewModel.formatNutritionValue(nutrition.vitaminB)} (g)"
                binding.nutritionVitaminC.text = "Vitamin C\n${viewModel.formatNutritionValue(nutrition.vitaminC)} (g)"
            }
        }

        // Back button
        binding.backButton.setOnClickListener { finish() }
    }
}