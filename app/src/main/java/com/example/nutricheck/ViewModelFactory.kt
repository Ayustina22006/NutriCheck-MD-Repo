package com.example.nutricheck

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nutricheck.auth.login.LoginViewModel
import com.example.nutricheck.auth.register.AssessmentViewModel
import com.example.nutricheck.auth.register.RegisterViewModel
import com.example.nutricheck.data.Injection
import com.example.nutricheck.data.ResourceProvider
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.ui.add.AddSearchViewModel
import com.example.nutricheck.ui.add.AddViewModel
import com.example.nutricheck.ui.history.HistoryDetailViewModel
import com.example.nutricheck.ui.history.HistoryViewModel
import com.example.nutricheck.ui.home.HomeViewModel
import com.example.nutricheck.ui.nutrition.NutritionViewModel
import com.example.nutricheck.ui.onboarding.OnBoardingViewModel
//import com.example.nutricheck.ui.pedia.ArticleViewModel
import com.example.nutricheck.ui.profil.ProfilViewModel
import com.example.nutricheck.ui.scan.CameraViewModel

class ViewModelFactory(
    private val repository: UserRepository,
    private val resourceProvider: ResourceProvider,
    private val application: Application
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(repository, resourceProvider) as T
            }
//            modelClass.isAssignableFrom(ArticleViewModel::class.java) -> {
//                ArticleViewModel(repository) as T
//            }
            modelClass.isAssignableFrom(AssessmentViewModel::class.java) -> {
                AssessmentViewModel(repository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(OnBoardingViewModel::class.java) -> {
                OnBoardingViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfilViewModel::class.java) -> {
                ProfilViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CameraViewModel::class.java) -> {
                CameraViewModel(application, repository) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddSearchViewModel::class.java) -> {
                AddSearchViewModel(repository) as T
            }
            modelClass.isAssignableFrom(NutritionViewModel::class.java) -> {
                NutritionViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(HistoryDetailViewModel::class.java) -> {
                HistoryDetailViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                val userRepository = Injection.provideRepository(context)
                val resourceProvider = Injection.provideResourceProvider(context)
                val application = context.applicationContext as Application

                INSTANCE = ViewModelFactory(userRepository, resourceProvider, application)
                INSTANCE!!
            }
        }
    }
}


