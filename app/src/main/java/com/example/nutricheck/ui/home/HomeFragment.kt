package com.example.nutricheck.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.databinding.FragmentHomeBinding
import com.example.nutricheck.ui.ArticleAdapter
import androidx.appcompat.widget.Toolbar

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var articleAdapter: ArticleAdapter
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val factory = ViewModelFactory.getInstance(requireContext())
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.visibility = View.GONE

        // Ambil userId dari session
        homeViewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                user.userId.let { userId ->
                    homeViewModel.fetchDailyCalories(userId)
                }
            }
        }

        homeViewModel.calorieResult.observe(viewLifecycleOwner) { totalCalories ->
            if (totalCalories != null) {
                val caloriesConsumed = 773f // Ganti dengan nilai aktual
                val progress = (caloriesConsumed / totalCalories) * 100

                Log.d("HomeFragment", "Total Calories: $totalCalories")
                Log.d("HomeFragment", "Calories Consumed: $caloriesConsumed")
                Log.d("HomeFragment", "Progress: $progress")
                Log.d("HomeFragment", "Calories observed: $totalCalories")


                binding.semiCircularProgressBar.setTotalCalories(totalCalories)
                binding.semiCircularProgressBar.setProgress(progress)
                binding.tvCaloriesStatus.text = "$caloriesConsumed/${totalCalories.toInt()} kalori terpenuhi"
            } else {
                Log.e("HomeFragment", "Total calories result is null")
            }
        }

        articleAdapter = ArticleAdapter()
        binding.rvArticle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = articleAdapter
        }

        // Observe LiveData untuk artikel
        homeViewModel.articles.observe(viewLifecycleOwner) { articles ->
            articles?.let {
                articleAdapter.submitList(it)
            }
        }

        homeViewModel.fetchArticles()
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
