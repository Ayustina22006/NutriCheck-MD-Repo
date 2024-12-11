package com.example.nutricheck.ui.onboarding.welcomefragment

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.nutricheck.R
import com.example.nutricheck.databinding.FragmentWelcome4Binding

class WelcomeFragment4 : Fragment() {

    private var _binding: FragmentWelcome4Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcome4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment4_to_RegisterActivity)
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment4_to_welcomeFragment3)
        }

        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment4_to_RegisterActivity)
        }

        playAnimation()
    }
    private fun playAnimation() {
        val progressBar = ObjectAnimator.ofFloat(binding.progressBar, View.ALPHA, 1f).setDuration(500)
        progressBar.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
