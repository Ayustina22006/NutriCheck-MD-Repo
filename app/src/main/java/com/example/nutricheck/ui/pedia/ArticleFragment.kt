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
import com.example.nutricheck.databinding.FragmentArticleBinding
import com.example.nutricheck.ui.ArticleAdapter

class ArticleFragment : Fragment() {

    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var binding: FragmentArticleBinding
    private lateinit var articleAdapter: ArticleAdapter

    // Fungsi untuk menyembunyikan keyboard
    private fun hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout untuk fragment ini
        binding = FragmentArticleBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel
        val factory = ViewModelFactory.getInstance(requireContext())
        articleViewModel = ViewModelProvider(this, factory)[ArticleViewModel::class.java]

        // Inisialisasi RecyclerView Adapter
        articleAdapter = ArticleAdapter()
        binding.recyclerViewArticles.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewArticles.adapter = articleAdapter

        // Observe LiveData dari ViewModel
        articleViewModel.articles.observe(viewLifecycleOwner) { articles ->
            articleAdapter.submitList(articles)
        }

        // Fungsi pencarian artikel
        binding.searchView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword = binding.searchView.text.toString().trim()
                if (keyword.isNotEmpty()) {
                    articleViewModel.searchArticles(keyword)
                }
                hideKeyboard(binding.searchView) // Menutup keyboard
                true // Mengindikasikan aksi sudah ditangani
            } else {
                false
            }
        }


        // Tombol kategori "All"
        binding.btnCategoryAll.setOnClickListener {
            articleViewModel.fetchArticles()
        }

        // Tombol kategori "News"
        binding.btnCategoryNews.setOnClickListener {
            articleViewModel.filterArticlesByCategory("News")
        }

        // Tombol kategori "Food"
        binding.btnCategoryFood.setOnClickListener {
            articleViewModel.filterArticlesByCategory("Food")
        }

        // Tombol kategori "Health"
        binding.btnCategoryHealth.setOnClickListener {
            articleViewModel.filterArticlesByCategory("Healthy")
        }

        // Mengambil artikel saat pertama kali fragment dibuat
        articleViewModel.fetchArticles()

        return binding.root
    }
}
