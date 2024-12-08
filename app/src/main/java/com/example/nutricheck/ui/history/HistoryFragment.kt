package com.example.nutricheck.ui.history

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nutricheck.R
import com.example.nutricheck.databinding.FragmentHistoryBinding
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("NewApi")
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

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
        setupInitialView()

        binding.btnPrevious.setOnClickListener { onPreviousClicked() }
        binding.btnNext.setOnClickListener { onNextClicked() }
        binding.tvDate.setOnClickListener {
            showDatePickerDialog()
        }
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
        }
    }

    private fun onNextClicked() {
        if (currentDate < todayDate) {
            currentDate = currentDate.plusDays(1)
            updateDateDisplay()
        }
    }

    private fun updateDateDisplay() {
        val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        val todayText = "Today"

        binding.tvDate.text = if (currentDate == todayDate) {
            binding.btnNext.isEnabled = false
            todayText
        } else {
            binding.btnNext.isEnabled = true
            currentDate.format(dateFormatter)
        }

        binding.btnPrevious.isEnabled = currentDate > earliestDate
        adjustTextViewWidth()
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
            R.style.CustomDatePickerTheme, // Gunakan tema kustom di sini
            { _, year, month, dayOfMonth ->
                val selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
                if (selectedDate.isAfter(todayDate)) return@DatePickerDialog
                if (selectedDate.isBefore(earliestDate)) return@DatePickerDialog
                currentDate = selectedDate
                updateDateDisplay()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()

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
