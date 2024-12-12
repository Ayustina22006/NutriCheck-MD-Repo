package com.example.nutricheck.ui.profil

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.nutricheck.R
import com.example.nutricheck.ViewModelFactory
import com.example.nutricheck.auth.login.LoginActivity
import com.example.nutricheck.databinding.FragmentProfilBinding


class ProfilFragment : Fragment() {
    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfilViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            if (user.isLogin) {
                viewModel.fetchUserProfile(user.userId)
                viewModel.fetchDailyCalories(user.userId)
//                viewModel.fetchNutritionData("2024-12-12")
            } else {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }

        observeViewModel()

        setupClickListeners()
    }

    private fun observeViewModel() {

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }

        viewModel.username.observe(viewLifecycleOwner) { username ->
            val initials = viewModel.getInitials(username) // Ambil inisial
            val initialsDrawable = createProfileInitialsDrawable(initials) // Buat drawable lingkaran
            binding.profileUser.setImageBitmap(initialsDrawable) // Tampilkan di ImageView
        }

        viewModel.bmiResult.observe(viewLifecycleOwner) { bmiValue ->
            binding.tvBMI.text = String.format("BMI: %.1f", bmiValue)

            val bmiStatus = when {
                bmiValue < 18.5 -> "Sangat Kurus"
                bmiValue < 25 -> "Normal"
                bmiValue < 30 -> "Overweight"
                else -> "Obesitas"
            }
            binding.tvBMIStatus.text = bmiStatus
        }

        viewModel.dailyCalories.observe(viewLifecycleOwner) { totalCalories ->
            binding.tvTargetKaloriStatus.text = if (totalCalories != null) {
                "$totalCalories kcal"
            } else {
                "Calorie User Not FOund"
            }
        }

        viewModel.username.observe(viewLifecycleOwner) { username ->
            binding.tvCurrentDate.text = username
        }


        viewModel.fetchStatus.observe(viewLifecycleOwner) { status ->
            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
        }

        viewModel.logoutStatus.observe(viewLifecycleOwner) { isLoggedOut ->
            if (isLoggedOut) {
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    private fun setupClickListeners() {
        binding.tvEditUserProfile.setOnClickListener {
            findNavController().navigate(R.id.action_user_to_edit)
        }

        binding.tvHealthAssesment.setOnClickListener {
            findNavController().navigate(R.id.action_user_to_asessment)
        }

        binding.buttonLogout.setOnClickListener {
            viewModel.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createProfileInitialsDrawable(initials: String): Bitmap {
        val size = 100
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        paint.color = Color.LTGRAY
        paint.style = Paint.Style.FILL
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        paint.color = Color.BLACK
        paint.textSize = 40f
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val textY = (canvas.height / 2 - (paint.descent() + paint.ascent()) / 2)
        canvas.drawText(initials, (canvas.width / 2).toFloat(), textY, paint)

        return bitmap
    }
}
