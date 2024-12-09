package com.example.nutricheck.ui.history

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.data.response.MealDetail
import com.example.nutricheck.databinding.ActivityHistoryDetailBinding
import com.example.nutricheck.ui.NutritionDetailsAdapter

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding
    private lateinit var viewModel: HistoryDetailViewModel
    private lateinit var nutritionDetailsAdapter: NutritionDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi View Binding
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi ViewModel
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[HistoryDetailViewModel::class.java]

        // Inisialisasi Adapter
        nutritionDetailsAdapter = NutritionDetailsAdapter { mealDetail ->
            // Optional: Handle item click if needed
            // For example, show a dialog or navigate to detail page
        }

        // Setup RecyclerView
        binding.rvViewNutrition.apply {
            layoutManager = LinearLayoutManager(this@HistoryDetailActivity)
            adapter = nutritionDetailsAdapter
        }

        // Ambil data dari intent
        val mealType = intent.getStringExtra("MEAL_TYPE") ?: ""
        val date = intent.getStringExtra("DATE")

        // Panggil fungsi untuk fetch data
        viewModel.fetchDetailHistory(mealType, date)

        // Observasi data dari ViewModel
        observeViewModel()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        viewModel.detailHistory.observe(this) { detailHistory ->
            detailHistory?.let { history ->
                val firstMealDetail = history.data?.firstOrNull()?.mealsDetails?.firstOrNull()
                val totalNutrition = history.data?.firstOrNull()?.totalNutrition

                binding.tvTitle.text = getString(R.string.nutrition_analisys)

                history.data?.firstOrNull()?.let { dataItem ->
                    val calories = dataItem.totalCalories ?: 0
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
                    binding.textKalori.text = "Total Calories: $calories kcal"
                }

                totalNutrition?.let { nutrition ->
                    binding.nutritionProtein.text = "Protein\n${nutrition.protein ?: 0} g"
                    binding.nutritionCalories.text = "Calories\n${nutrition.calcium ?: 0} mg"
                    binding.nutritionDietaryFiber.text = "Dietary Fiber\n${nutrition.dietaryFiber ?: 0} g"
                    binding.nutritionCalcium.text = "Calcium\n${nutrition.calcium ?: 0} mg"
                    binding.nutritionIron.text = "Iron\n${nutrition.iron ?: 0} mg"
                    binding.nutritionCarbohydrate.text = "Carbohydrate\n${nutrition.carbohydrate ?: 0} g"
                    binding.nutritionVitaminA.text = "Vitamin A\n${nutrition.vitaminA ?: 0} IU"
                    binding.nutritionVitaminB.text = "Vitamin B\n${nutrition.vitaminB ?: 0} mg"
                    binding.nutritionVitaminC.text = "Vitamin C\n${nutrition.vitaminC ?: 0} mg"
                }

                // Update RecyclerView with meal details
//                history.data?.firstOrNull()?.mealsDetails?.let { mealDetails ->
//                    nutritionDetailsAdapter.submitList(mealDetails)
//                }
            }
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}