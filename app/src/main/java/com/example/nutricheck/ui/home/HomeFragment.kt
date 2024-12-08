package com.example.nutricheck.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.databinding.FragmentHomeBinding
import com.example.nutricheck.ui.ArticleAdapter
import com.example.nutricheck.data.response.Food
import com.example.nutricheck.ui.add.AddActivity
import com.example.nutricheck.ui.add.AddSearchActivity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import com.example.nutricheck.ui.NutritionAdapter
import com.example.nutricheck.databinding.AlertBinding
import com.example.nutricheck.ui.scan.ScanActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var articleAdapter: ArticleAdapter
    private var alertDialog: AlertDialog? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as? AppCompatActivity)?.supportActionBar?.hide()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireContext())
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        setupSearchView()
        setupObservers()
        setupArticleRecyclerView()
        setupNutritionsRecyclerView()

        binding.etAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddActivity::class.java)
            startActivity(intent)
        }
        binding.etScan.setOnClickListener {
            val intent = Intent(requireContext(), ScanActivity::class.java)
            startActivity(intent)
        }

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        homeViewModel.fetchNutritionData(currentDate)
        Log.d("HomeFragment", "date in day: $currentDate")
//        homeViewModel.fetchNutritionData("2024-12-07")

    }


    private fun setupSearchView() {
        binding.searchView.setOnClickListener {
            binding.searchView.isIconified = false
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    homeViewModel.searchFood(it.trim()) // Lakukan pencarian
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }



    private fun setupObservers() {
        homeViewModel.foodNutritionResult.observe(viewLifecycleOwner) { food ->
            food?.let {
                navigateToAddSearchActivity(it)
                homeViewModel.resetFoodNutritionResult()
            }
        }

        homeViewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                showFoodNotFoundAlert(it)
                homeViewModel.resetErrorMessage()
            }
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        homeViewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                user.userId.let { userId ->
                    homeViewModel.fetchDailyCalories(userId)
                }
            }
        }

        homeViewModel.caloriesConsumed.observe(viewLifecycleOwner) { caloriesConsumed ->
            homeViewModel.calorieResult.observe(viewLifecycleOwner) { totalCalories ->
                if (totalCalories != null) {
                    val progress = (caloriesConsumed / totalCalories) * 100

                    Log.d("HomeFragment", "Total Calories: $totalCalories")
                    Log.d("HomeFragment", "Calories Consumed: $caloriesConsumed")
                    Log.d("HomeFragment", "Progress: $progress")
                    Log.d("HomeFragment", "Calories observed: $totalCalories")

                    binding.semiCircularProgressBar.setMax(totalCalories)
                    binding.semiCircularProgressBar.setTotalCalories(totalCalories)
                    binding.semiCircularProgressBar.setProgress(caloriesConsumed)

                    binding.tvCaloriesStatus.text = "${caloriesConsumed.toInt()}/${totalCalories.toInt()} calories fulfilled"
                }
            }
        }
    }

    private fun setupNutritionsRecyclerView() {
        val nutritionAdapter = NutritionAdapter()
        binding.rvNutrition.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = nutritionAdapter
        }

        homeViewModel.nutritionData.observe(viewLifecycleOwner) { nutritionItems ->
            nutritionAdapter.setNutritionList(nutritionItems)
        }
    }

    private fun setupArticleRecyclerView() {
        articleAdapter = ArticleAdapter()
        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = articleAdapter
        }

        homeViewModel.articles.observe(viewLifecycleOwner) { articles ->
            articles?.let {
                articleAdapter.submitList(it)
            }
        }

        homeViewModel.fetchArticles()
    }

    private fun showFoodNotFoundAlert(message: String) {
        // Cek apakah dialog sudah ada
        if (alertDialog == null || !alertDialog!!.isShowing) {
            val dialogBinding = AlertBinding.inflate(LayoutInflater.from(requireContext()))
            dialogBinding.tvAlertMessage.text = message

            alertDialog = AlertDialog.Builder(requireContext())
                .setView(dialogBinding.root)
                .create()

            dialogBinding.btnAddFood.setOnClickListener {
                val intent = Intent(requireContext(), AddActivity::class.java)
                startActivity(intent)
                alertDialog?.dismiss()
                homeViewModel.resetErrorMessage()
            }

            dialogBinding.btnCancel.setOnClickListener {
                alertDialog?.dismiss()
                homeViewModel.resetErrorMessage()
            }

            alertDialog?.setOnDismissListener {
                homeViewModel.resetErrorMessage()
            }

            alertDialog?.show()
        }
    }


    private fun navigateToAddSearchActivity(food: Food) {
        val intent = Intent(requireContext(), AddSearchActivity::class.java).apply {
            putExtra(AddSearchActivity.EXTRA_FOOD, food)
        }
        startActivity(intent)
        homeViewModel.resetFoodNutritionResult()

    }

    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        alertDialog?.dismiss()
        alertDialog = null
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        homeViewModel.errorMessage.value?.let {
            showFoodNotFoundAlert(it)
            homeViewModel.resetErrorMessage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
