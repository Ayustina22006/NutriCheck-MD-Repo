package com.example.nutricheck.ui.onboarding.welcomefragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutricheck.R
import com.example.nutricheck.databinding.FragmentWelcome2Binding

class WelcomeFragment2 : Fragment() {

    private var _binding: FragmentWelcome2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcome2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigasi ke fragment berikutnya
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment2_to_welcomeFragment3)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment2_to_welcomeFragment1)
        }

        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment2_to_loginActivity)
        }
        playAnimation()
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        binding.progressBar.visibility = View.VISIBLE
        binding.imageViewContent.visibility = View.VISIBLE
        binding.btnNext.visibility = View.VISIBLE

        val progressBar = ObjectAnimator.ofFloat(binding.progressBar, View.ALPHA, 1f).setDuration(500)
        val imageViewContent = ObjectAnimator.ofFloat(binding.imageViewContent, View.ALPHA, 1f).setDuration(500)
        val btnNext = ObjectAnimator.ofFloat(binding.btnNext, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(progressBar, imageViewContent, btnNext)
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
