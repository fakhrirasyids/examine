package com.example.examine.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.examine.data.remote.ApiService
import com.example.examine.data.repo.AuthRepository
import com.example.examine.data.repo.TestRepository
import com.example.examine.ui.main.home.HomeViewModel
import com.example.examine.ui.main.profile.ProfileViewModel
import com.example.examine.utils.UserPreferences

class MainViewModelFactory constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(TestRepository(apiService),userPreferences) as T
        } else if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            ProfileViewModel(AuthRepository(apiService), userPreferences) as T
        } else
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}