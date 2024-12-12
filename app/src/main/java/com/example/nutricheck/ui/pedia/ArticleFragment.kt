package com.example.nutricheck.ui.pedia

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.data.entity.ArticleEntity
import com.example.nutricheck.databinding.FragmentArticleBinding
import com.example.nutricheck.ui.ArticleAdapter

class ArticleFragment : Fragment() {

    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var binding: FragmentArticleBinding
    private lateinit var articleAdapter: ArticleAdapter

    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArticleBinding.inflate(inflater, container, false)

        val factory = ViewModelFactory.getInstance(requireContext())
        articleViewModel = ViewModelProvider(this, factory)[ArticleViewModel::class.java]

        articleAdapter = ArticleAdapter()
        binding.recyclerViewArticles.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewArticles.adapter = articleAdapter

        // Observe articles
        articleViewModel.articles.observe(viewLifecycleOwner) { articles ->
            articleAdapter.submitList(articles)
        }

        // Observe loading state
//        articleViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//        }

        articleViewModel.noArticlesAvailable.observe(viewLifecycleOwner) { noArticles ->
            // Toggle visibility of RecyclerView and Empty State TextView
            binding.recyclerViewArticles.visibility = if (noArticles) View.GONE else View.VISIBLE
            binding.emptyStateText.visibility = if (noArticles) View.VISIBLE else View.GONE
        }

        // Search functionality
        binding.searchView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword = binding.searchView.text.toString().trim()
                if (keyword.isNotEmpty()) {
                    articleViewModel.searchArticles(keyword)
                }
                hideKeyboard(binding.searchView)
                true
            } else {
                false
            }
        }

        // Category buttons
        binding.btnCategoryAll.setOnClickListener { articleViewModel.fetchArticles() }
        binding.btnCategoryNews.setOnClickListener { articleViewModel.filterArticlesByCategory("News") }
        binding.btnCategoryFood.setOnClickListener { articleViewModel.filterArticlesByCategory("Food") }
        binding.btnCategoryHealth.setOnClickListener { articleViewModel.filterArticlesByCategory("Health") }

        // Initial fetch
        articleViewModel.fetchArticles()
        // Get all articles
        articleViewModel.filterArticlesByCategory("News")
        articleViewModel.filterArticlesByCategory("Food")
        articleViewModel.filterArticlesByCategory("Health")
        return binding.root
    }
}
