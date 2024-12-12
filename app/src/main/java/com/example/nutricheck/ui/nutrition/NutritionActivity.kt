package com.example.nutricheck.ui.nutrition

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutricheck.MainActivity
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.data.response.MealHistoryResponse
import com.example.nutricheck.databinding.ActivityNutritionBinding
import com.example.nutricheck.ui.NutritionDetailsAdapter
import com.example.nutricheck.ui.home.HomeFragment

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

        binding.piringku.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse("https://ayosehat.kemkes.go.id/isi-piringku-pedoman-makan-kekinian-orang-indonesia")
            startActivity(intent)
            true
        }

        mealHistoryResponse?.mealHistory?.mealsDetails?.let {
            nutritionDetailsAdapter.submitList(it)
        }
        // Observe data
        viewModel.mealDetails.observe(this) { mealDetails ->
            nutritionDetailsAdapter.submitList(mealDetails)
        }

        viewModel.mealHistory.observe(this) { mealHistory ->
            mealHistory?.totalNutrition?.let { nutrition ->
                binding.nutritionProtein.text =
                    "${viewModel.formatNutritionValue(nutrition.protein)} (g)"
                binding.nutritionCalories.text = "${mealHistory.totalCalories} (kcal)"
                binding.nutritionDietaryFiber.text =
                    "${viewModel.formatNutritionValue(nutrition.dietaryFiber)} (g)"
                binding.nutritionCalcium.text =
                    "${viewModel.formatNutritionValue(nutrition.calcium)} (g)"
                binding.nutritionIron.text = "${viewModel.formatNutritionValue(nutrition.iron)} (g)"
                binding.nutritionCarbohydrate.text =
                    "${viewModel.formatNutritionValue(nutrition.carbohydrate)} (g)"
                binding.nutritionVitaminA.text =
                    "\n${viewModel.formatNutritionValue(nutrition.vitaminA)} (g)"
                binding.nutritionVitaminB.text =
                    "${viewModel.formatNutritionValue(nutrition.vitaminB)} (g)"
                binding.nutritionVitaminC.text =
                    "${viewModel.formatNutritionValue(nutrition.vitaminC)} (g)"

                // Group nutrients
                val proteinSources = listOf(
                    nutrition.protein,
                    nutrition.calcium,
                    nutrition.iron
                )

                val stapleFoods = listOf(
                    nutrition.carbohydrate
                )

                val vegetables = listOf(
                    nutrition.vitaminA,
                    nutrition.vitaminB,
                    nutrition.vitaminC,
                    nutrition.dietaryFiber
                )

                fun determineGroupStatus(group: List<Double?>, thresholds: List<Double>): String {
                    // Replace null values with 0.0 to allow summation
                    val totalGroup = group.map { it ?: 0.0 }.sum()
                    return when {
                        totalGroup < thresholds[0] -> "LOW"
                        totalGroup > thresholds[1] -> "EXCESSIVE"
                        else -> "IDEAL"
                    }
                }

                val proteinSourcesStatus = determineGroupStatus(proteinSources, listOf(30.0, 60.0))
                val stapleFoodsStatus = determineGroupStatus(stapleFoods, listOf(50.0, 100.0))
                val vegetablesStatus = determineGroupStatus(vegetables, listOf(40.0, 80.0))

                val calories = mealHistory.totalCalories

                val alertMessage = when {
                    calories!! < 500 -> when {
                        vegetablesStatus == "LOW" && proteinSourcesStatus == "LOW" && stapleFoodsStatus == "LOW" ->
                            "Let's increase your calorie intake to provide enough energy for your body! Try adding more fresh vegetables. Add protein sources such as eggs, fish, or tofu. Don't forget to include rice or other staple foods for energy."

                        vegetablesStatus == "IDEAL" && proteinSourcesStatus == "LOW" && stapleFoodsStatus == "LOW" ->
                            "Let's increase your calorie intake to provide enough energy for your body! Your vegetable intake is great, keep it up! Add protein sources such as eggs, fish, or tofu. Don't forget to include rice or other staple foods for energy."

                        vegetablesStatus == "EXCESSIVE" && proteinSourcesStatus == "LOW" && stapleFoodsStatus == "LOW" ->
                            "Let's increase your calorie intake to provide enough energy for your body! Although your vegetable intake is high, try balancing it with protein sources and staple foods. Add protein sources such as eggs, fish, or tofu. Don't forget to include rice or other staple foods."

                        vegetablesStatus == "LOW" && proteinSourcesStatus == "IDEAL" && stapleFoodsStatus == "LOW" ->
                            "Let's increase your calorie intake to provide enough energy for your body! Try adding more fresh vegetables. Your protein intake is perfect! Don't forget to include rice or other staple foods for energy."

                        else -> "Let's increase your calorie intake to provide enough energy for your body!"
                    }

                    calories.toDouble() in 500.0..700.0 ->
                        "Congratulations! Your calorie intake is ideal. Your portions of vegetables, protein sources, and staple foods are well-balanced!"

                    calories.toDouble() in 701.0..800.0 -> when {
                        vegetablesStatus == "EXCESSIVE" && proteinSourcesStatus == "EXCESSIVE" && stapleFoodsStatus == "EXCESSIVE" ->
                            "Your calorie intake slightly exceeds your needs. Reduce the use of sauces on vegetables. Cut down on protein portions and choose lean options. Reduce the portion of rice or other staple foods."

                        vegetablesStatus == "LOW" && proteinSourcesStatus == "EXCESSIVE" && stapleFoodsStatus == "EXCESSIVE" ->
                            "Your calorie intake slightly exceeds your needs. Add more fresh vegetables and reduce other portions. Cut down on protein portions and choose lean options. Reduce the portion of rice or other staple foods."

                        vegetablesStatus == "IDEAL" && proteinSourcesStatus == "EXCESSIVE" && stapleFoodsStatus == "EXCESSIVE" ->
                            "Your calorie intake slightly exceeds your needs. Your vegetable intake is good. Cut down on protein portions and choose lean options. Reduce the portion of rice or other staple foods."

                        else -> "Your calorie intake slightly exceeds your needs."
                    }

                    calories > 800 -> when {
                        vegetablesStatus == "EXCESSIVE" && proteinSourcesStatus == "EXCESSIVE" && stapleFoodsStatus == "EXCESSIVE" ->
                            "Your calorie intake is too high! Reduce the use of sauces on vegetables. Drastically cut down protein portions, choose low-fat options. Significantly reduce the portion of rice or other staple foods."

                        vegetablesStatus == "LOW" && proteinSourcesStatus == "EXCESSIVE" && stapleFoodsStatus == "EXCESSIVE" ->
                            "Your calorie intake is too high! Add more fresh vegetables. Drastically cut down protein portions, choose low-fat options. Significantly reduce the portion of rice or other staple foods."

                        vegetablesStatus == "IDEAL" && proteinSourcesStatus == "EXCESSIVE" && stapleFoodsStatus == "EXCESSIVE" ->
                            "Your calorie intake is too high! Maintain your vegetable intake. Drastically cut down protein portions, choose low-fat options. Significantly reduce the portion of rice or other staple foods."

                        else -> "Your calorie intake is too high!"
                    }

                    else -> "Unable to determine calorie intake status"
                }

                // Update teks binding
                binding.textKalori.text = alertMessage
                when {
                    calories < 500 -> {
                        binding.profilePlaceholder.setImageResource(R.drawable.kurang)
                        binding.textAlert.text = binding.root.context.getString(R.string.alert_kurang)
                    }
                    calories in 500..700 -> {
                        binding.profilePlaceholder.setImageResource(R.drawable.ideal)
                        binding.textAlert.text = binding.root.context.getString(R.string.alert_ideal)
                    }
                    calories in 701..800 -> {
                        binding.profilePlaceholder.setImageResource(R.drawable.berlebih)
                        binding.textAlert.text = binding.root.context.getString(R.string.alert_berlebih)
                    }
                    calories > 800 -> {
                        binding.profilePlaceholder.setImageResource(R.drawable.sangat_berlebih)
                        binding.textAlert.text = binding.root.context.getString(R.string.alert_berlebihan)
                    }
                }
            }
        }

        // Back button
        binding.backButton.setOnClickListener {
            val Intent = Intent(this@NutritionActivity, MainActivity::class.java)
            startActivity(Intent)
            finish()
        }

    }
}