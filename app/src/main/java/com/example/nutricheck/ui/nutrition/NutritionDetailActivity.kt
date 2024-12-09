package com.example.nutricheck.ui.nutrition

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nutricheck.R
import com.example.nutricheck.data.response.MealDetail
import com.example.nutricheck.databinding.ActivityNutritionDetailBinding

class NutritionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNutritionDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNutritionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mealDetail = intent.getParcelableExtra<MealDetail>("MEAL_DETAIL")

        mealDetail?.let { detail ->
            // Header Section
            binding.titleText.text = detail.foodName ?: "Unknown Food"
            binding.servingSizeValue.text = "${detail.servingSize ?: 0} g"
            binding.calorieValue.text = "${detail.calories ?: 0} kcal"

            // Makronutrien
            val nutritionMap = detail.nutritionDetails?.toMap() ?: emptyMap()
            binding.proteinMacro.text = getString(R.string.protein_macro, nutritionMap["Protein"] ?: "0 g")
            binding.carbohydrateMacro.text = getString(R.string.carbohydrate_macro, nutritionMap["Carbohydrate"] ?: "0 g")

            // Mikronutrien
            binding.calciumMicro.text = getString(R.string.calcium_micro, nutritionMap["Calcium"] ?: "0 g")
            binding.dietaryFiberMicro.text = getString(R.string.dietary_fiber_micro, nutritionMap["Dietary Fiber"] ?: "0 g")
            binding.ironMicro.text = getString(R.string.iron_micro, nutritionMap["Iron"] ?: "0 mg")
            binding.vitaminAMicro.text = getString(R.string.vitamin_a_micro, nutritionMap["Vitamin A"] ?: "0 g")
            binding.vitaminBMicro.text = getString(R.string.vitamin_b_micro, nutritionMap["Vitamin B"] ?: "0 g")
            binding.vitaminCMicro.text = getString(R.string.vitamin_c_micro, nutritionMap["Vitamin C"] ?: "0 g")

            // Update Tag Label
            val maxNutrient = nutritionMap.maxByOrNull { entry ->
                extractNumericValue(entry.value.toString())
            }
            maxNutrient?.let {
                binding.tagLabel.text = it.key
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    // Fungsi untuk mengekstrak nilai numerik dari string
    private fun extractNumericValue(value: String): Double {
        return value.replace("[^\\d.]".toRegex(), "").toDoubleOrNull() ?: 0.0
    }
}

