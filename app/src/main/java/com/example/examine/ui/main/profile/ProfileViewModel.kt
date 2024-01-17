package com.example.examine.ui.main.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.examine.data.repo.AuthRepository
import com.example.examine.utils.UserPreferences
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    fun logout() = authRepository.logoutUser()

    fun clearPreferences() {
        viewModelScope.launch {
            userPreferences.clearPreferences()
        }
    }

    fun getFullname() = userPreferences.getUsername().asLiveData()
    fun getEmail() = userPreferences.getEmail().asLiveData()
}