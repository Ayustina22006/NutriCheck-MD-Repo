package com.example.nutricheck.ui.history

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.databinding.ActivityHistoryDetailBinding
import com.example.nutricheck.ui.DetailHistoryActivity

class HistoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDetailBinding
    private lateinit var viewModel: HistoryDetailViewModel
    private lateinit var historyDetailAdapter: HistoryDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[HistoryDetailViewModel::class.java]

        val mealType = intent.getStringExtra("MEAL_TYPE") ?: ""
        val date = intent.getStringExtra("DATE")

        historyDetailAdapter = HistoryDetailAdapter { MealsDetailsHistoryItem ->
            Log.d("HistoryDetailActivity", "Item clicked: $MealsDetailsHistoryItem")
            val intent = Intent(this, DetailHistoryActivity::class.java).apply {
                putExtra("MEAL_DETAIL", MealsDetailsHistoryItem)
            }
            startActivity(intent)
        }


        binding.rvViewNutrition.apply {
            layoutManager = LinearLayoutManager(this@HistoryDetailActivity)
            adapter = historyDetailAdapter
        }


        viewModel.fetchDetailHistory(mealType, date)

        observeViewModel()

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.piringku.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = android.net.Uri.parse("https://ayosehat.kemkes.go.id/isi-piringku-pedoman-makan-kekinian-orang-indonesia")
            startActivity(intent)
            true
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
                    val nutrition = totalNutrition ?: return@let

                    // Determine nutrient group status
                    val proteinGroup = listOf(
                        nutrition.protein,
                        nutrition.calcium,
                        nutrition.iron
                    )
                    val carbGroup = listOf(
                        nutrition.carbohydrate
                    )
                    val vegetableGroup = listOf(
                        nutrition.vitaminA,
                        nutrition.vitaminB,
                        nutrition.vitaminC,
                        nutrition.dietaryFiber
                    )

                    fun determineGroupStatus(group: List<Double?>, thresholds: List<Double>): String {
                        val totalGroup = group.map { it ?: 0.0 }.sum()
                        return when {
                            totalGroup < thresholds[0] -> "INSUFFICIENT"
                            totalGroup > thresholds[1] -> "EXCESSIVE"
                            else -> "IDEAL"
                        }
                    }

                    val proteinGroupStatus = determineGroupStatus(proteinGroup, listOf(30.0, 60.0))
                    val carbGroupStatus = determineGroupStatus(carbGroup, listOf(50.0, 100.0))
                    val vegetableGroupStatus = determineGroupStatus(vegetableGroup, listOf(40.0, 80.0))

                    val alertMessage = when {
                        calories < 500 -> when {
                            vegetableGroupStatus == "INSUFFICIENT" && proteinGroupStatus == "INSUFFICIENT" && carbGroupStatus == "INSUFFICIENT" ->
                                "Let's increase your calorie intake to provide your body with enough energy! Try adding more fresh vegetables. Add protein like eggs, fish, or tofu. Don't forget to add rice or other staple foods for energy."

                            vegetableGroupStatus == "IDEAL" && proteinGroupStatus == "INSUFFICIENT" && carbGroupStatus == "INSUFFICIENT" ->
                                "Let's increase your calorie intake to provide your body with enough energy! Your vegetable portion is good, keep it up! Add protein like eggs, fish, or tofu. Don't forget to add rice or other staple foods for energy."

                            vegetableGroupStatus == "EXCESSIVE" && proteinGroupStatus == "INSUFFICIENT" && carbGroupStatus == "INSUFFICIENT" ->
                                "Let's increase your calorie intake to provide your body with enough energy! Although you have plenty of vegetables, try to balance it with protein and staple foods. Add protein like eggs, fish, or tofu. Don't forget to add rice or other staple foods."

                            vegetableGroupStatus == "INSUFFICIENT" && proteinGroupStatus == "IDEAL" && carbGroupStatus == "INSUFFICIENT" ->
                                "Let's increase your calorie intake to provide your body with enough energy! Try adding more fresh vegetables. Your protein portion is just right! Don't forget to add rice or other staple foods for energy."

                            else -> "Let's increase your calorie intake to provide your body with enough energy!"
                        }

                        calories in 500..700 -> "Congratulations! Your calorie intake is ideal. Vegetable, protein, and staple food portions are balanced!"

                        calories in 701..800 -> when {
                            vegetableGroupStatus == "EXCESSIVE" && proteinGroupStatus == "EXCESSIVE" && carbGroupStatus == "EXCESSIVE" ->
                                "Your calorie intake is slightly above requirements. Reduce sauce on vegetables. Reduce protein portions and choose low-fat options. Reduce rice or staple food portions."

                            vegetableGroupStatus == "INSUFFICIENT" && proteinGroupStatus == "EXCESSIVE" && carbGroupStatus == "EXCESSIVE" ->
                                "Your calorie intake is slightly above requirements. Add fresh vegetables and reduce other portions. Reduce protein portions and choose low-fat options. Reduce rice or staple food portions."

                            vegetableGroupStatus == "IDEAL" && proteinGroupStatus == "EXCESSIVE" && carbGroupStatus == "EXCESSIVE" ->
                                "Your calorie intake is slightly above requirements. Vegetable portion is good. Reduce protein portions and choose low-fat options. Reduce rice or staple food portions."

                            else -> "Your calorie intake is slightly above requirements."
                        }

                        calories > 800 -> when {
                            vegetableGroupStatus == "EXCESSIVE" && proteinGroupStatus == "EXCESSIVE" && carbGroupStatus == "EXCESSIVE" ->
                                "Your calorie intake is too high! Reduce sauce on vegetables. Drastically reduce protein portions, choose low-fat options. Significantly reduce rice or staple food portions."

                            vegetableGroupStatus == "INSUFFICIENT" && proteinGroupStatus == "EXCESSIVE" && carbGroupStatus == "EXCESSIVE" ->
                                "Your calorie intake is too high! Add fresh vegetables. Drastically reduce protein portions, choose low-fat options. Significantly reduce rice or staple food portions."

                            vegetableGroupStatus == "IDEAL" && proteinGroupStatus == "EXCESSIVE" && carbGroupStatus == "EXCESSIVE" ->
                                "Your calorie intake is too high! Maintain vegetable portion. Drastically reduce protein portions, choose low-fat options. Significantly reduce rice or staple food portions."

                            else -> "Your calorie intake is too high!"
                        }

                        else -> "Unable to determine calorie intake status"
                    }

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

                    // Display meal history details
                    history.data.firstOrNull()?.mealsDetails?.let { mealDetails ->
                        historyDetailAdapter.submitList(mealDetails)
                    }

                    binding.nutritionCalories.text = "${dataItem.totalCalories ?: 0} kkal"
                    // Display nutrition details
                    totalNutrition.let { nutrition ->
                        binding.nutritionProtein.text = "${nutrition.protein ?: 0} g"
                        binding.nutritionDietaryFiber.text = "${nutrition.dietaryFiber ?: 0} g"
                        binding.nutritionCalcium.text = "${nutrition.calcium ?: 0} mg"
                        binding.nutritionIron.text = "${nutrition.iron ?: 0} mg"
                        binding.nutritionCarbohydrate.text = "${nutrition.carbohydrate ?: 0} g"
                        binding.nutritionVitaminA.text = "${nutrition.vitaminA ?: 0} IU"
                        binding.nutritionVitaminB.text = "${nutrition.vitaminB ?: 0} mg"
                        binding.nutritionVitaminC.text = "${nutrition.vitaminC ?: 0} mg"
                    }
                }
            }        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}