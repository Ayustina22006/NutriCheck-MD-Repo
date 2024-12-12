package com.example.nutricheck.ui.profil

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutricheck.data.Result
import com.example.nutricheck.data.UserRepository
import com.example.nutricheck.data.response.UpdateUserRequest
import com.example.nutricheck.data.response.UserResponse
import com.example.nutricheck.data.response.updateUserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditUserProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _updateResult = MutableLiveData<Result<UserResponse>>()
    val updateResult: LiveData<Result<UserResponse>> = _updateResult

    private val _updateResultBmi = MutableLiveData<Result<UserResponse>>()
    val updateResultBmi: LiveData<Result<UserResponse>> = _updateResultBmi

    fun updateGenderBMI(age: Int? = null, gender: String? = null) {
        viewModelScope.launch {
            val userId = userRepository.getSession().first().userId
            userRepository.updateBMI(userId, age = null, gender = gender).collect { result ->
                when (result) {
                    is Result.Loading -> _updateResultBmi.value = Result.Loading
                    is Result.Success -> _updateResultBmi.value = result
                    is Result.Error -> _updateResultBmi.value = Result.Error(result.error)
                }
            }
        }
    }

    fun updateAgeBMI(age: Int? = null, gender: String? = null) {
        viewModelScope.launch {
            val userId = userRepository.getSession().first().userId
            userRepository.updateBMI(userId, age = age, gender = null).collect { result ->
                when (result) {
                    is Result.Loading -> _updateResultBmi.value = Result.Loading
                    is Result.Success -> _updateResultBmi.value = result
                    is Result.Error -> _updateResultBmi.value = Result.Error(result.error)
                }
            }
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            userRepository.updateUser(
                userId = userRepository.getSession().first().userId,
                request = UpdateUserRequest(
                    username = newUsername,
                    email = null,
                    password = null
                )
            ).collect { result ->
                when (result) {
                    is Result.Loading -> _updateResult.value = Result.Loading
                    is Result.Success -> {
                        // Here, result is a UserResponse, not updateUserResponse
                        _updateResult.value = result
                    }
                    is Result.Error -> _updateResult.value = Result.Error(result.error)
                }
            }
        }
    }

    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            userRepository.updateUser(
                userId = userRepository.getSession().first().userId,
                request = UpdateUserRequest(
                    username = null,
                    email = newEmail,
                    password = null
                )
            ).collect { result ->
                when (result) {
                    is Result.Loading -> _updateResult.value = Result.Loading
                    is Result.Success -> {
                        // Here, result is a UserResponse, not updateUserResponse
                        _updateResult.value = result
                    }
                    is Result.Error -> _updateResult.value = Result.Error(result.error)
                }
            }
        }
    }

    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            userRepository.updateUser(
                userId = userRepository.getSession().first().userId,
                request = UpdateUserRequest(
                    username = null,
                    email = null,
                    password = newPassword
                )
            ).collect { result ->
                when (result) {
                    is Result.Loading -> _updateResult.value = Result.Loading
                    is Result.Success -> {
                        _updateResult.value = result
                    }
                    is Result.Error -> _updateResult.value = Result.Error(result.error)
                }
            }
        }
    }


    suspend fun fetchUserBMI(): Flow<Result<UserResponse>> {
        return viewModelScope.launch {
            val userId = userRepository.getSession().first().userId
            userRepository.fetchUserBMI(userId)
        }.let {
            userRepository.fetchUserBMI(userRepository.getSession().first().userId)
        }
    }
}