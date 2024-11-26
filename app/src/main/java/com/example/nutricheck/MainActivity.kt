package com.example.nutricheck

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.nutricheck.auth.login.LoginActivity
import com.example.nutricheck.databinding.ActivityMainBinding
import com.example.nutricheck.ui.onboarding.OnboardingActivity
import com.example.nutricheck.ui.scan.CameraActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this) // Gunakan ViewModelFactory untuk mendapatkan instance ViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cek status login menggunakan ViewModel
        mainViewModel.getSession().observe(this) { user ->
            if (user.token.isEmpty()) { // Jika token kosong, pengguna belum login
                navigateToLogin()
                return@observe
            }
        }

        // Inflate layout binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set Toolbar sebagai ActionBar
        setSupportActionBar(binding.toolbar)

        // Set up BottomNavigationView dengan NavController
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_pedia,
                R.id.navigation_history,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_pedia -> {
                    navController.navigate(R.id.navigation_pedia)
                    true
                }
                R.id.navigation_history -> {
                    navController.navigate(R.id.navigation_history)
                    true
                }
                R.id.navigation_profile -> {
                    navController.navigate(R.id.navigation_profile)
                    true
                }
                else -> false
            }
        }

        binding.scanButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    private fun navigateToLogin() {
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish() // Menghentikan aktivitas MainActivity agar tidak ada backstack
    }
}
