package com.example.nutricheck.ui.history

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.databinding.FragmentHistoryBinding
import com.example.nutricheck.ui.home.HomeViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("NewApi")
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var viewModel: HistoryViewModel

    private var currentDate = LocalDate.now()
    private val todayDate = LocalDate.now()
    private val earliestDate = todayDate.minusDays(6)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        setupRecyclerView()

        // Setup initial view
        setupInitialView()

        // Setup date navigation buttons
        binding.btnPrevious.setOnClickListener { onPreviousClicked() }
        binding.btnNext.setOnClickListener { onNextClicked() }
        binding.tvDate.setOnClickListener { showDatePickerDialog() }

        // Observe ViewModel
        observeViewModel()

        // Fetch initial history
        fetchHistoryForCurrentDate()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter { mealType, date ->
            val intent = Intent(requireContext(), HistoryDetailActivity::class.java).apply {
                putExtra("MEAL_TYPE", mealType)
                putExtra("DATE", date)
            }
            startActivity(intent)
        }

        binding.rvMealHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.historyData.observe(viewLifecycleOwner) { historyItems ->
            historyAdapter.submitList(historyItems)

            binding.emptyStateLayout.visibility = if (historyItems.isEmpty()) View.VISIBLE else View.GONE
            binding.rvMealHistory.visibility = if (historyItems.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchHistoryForCurrentDate() {
        viewModel.fetchMealHistory(currentDate)
    }

    private fun setupInitialView() {
        binding.tvDate.text = "Today"
        binding.btnNext.isEnabled = false
        binding.btnPrevious.isEnabled = true
        updateButtonStyles()
    }

    private fun onPreviousClicked() {
        if (currentDate > earliestDate) {
            currentDate = currentDate.minusDays(1)
            updateDateDisplay()
            fetchHistoryForCurrentDate()
        }
    }

    private fun onNextClicked() {
        if (currentDate < todayDate) {
            currentDate = currentDate.plusDays(1)
            updateDateDisplay()
            fetchHistoryForCurrentDate()
        }
    }

    private fun updateDateDisplay() {
        val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val todayText = "Today"
        val yesterdayText = "Yesterday"

        binding.tvDate.text = when (currentDate) {
            todayDate -> {
                binding.btnNext.isEnabled = false
                todayText
            }
            todayDate.minusDays(1) -> yesterdayText
            else -> {
                binding.btnNext.isEnabled = true
                currentDate.format(dateFormatter)
            }
        }

        binding.btnPrevious.isEnabled = currentDate > earliestDate
        adjustTextViewWidth()
        updateButtonStyles()
    }

    private fun adjustTextViewWidth() {
        val layoutParams = binding.tvDate.layoutParams as LinearLayout.LayoutParams
        layoutParams.weight = 1f
        binding.tvDate.layoutParams = layoutParams
    }

    private fun updateButtonStyles() {
        binding.btnNext.alpha = if (binding.btnNext.isEnabled) 1.0f else 0.5f
        binding.btnPrevious.alpha = if (binding.btnPrevious.isEnabled) 1.0f else 0.5f
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        calendar.time = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant())

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerTheme,
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)

                // Prevent selecting future dates
                if (selectedDate.isAfter(todayDate)) {
                    Toast.makeText(context, "Cannot select future dates", Toast.LENGTH_SHORT).show()
                    return@DatePickerDialog
                }

                currentDate = selectedDate
                updateDateDisplay()
                fetchHistoryForCurrentDate()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        (activity as? AppCompatActivity)?.supportActionBar?.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
