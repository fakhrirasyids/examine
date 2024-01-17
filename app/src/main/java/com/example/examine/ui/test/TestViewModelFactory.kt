package com.example.examine.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.examine.data.remote.ApiService
import com.example.examine.data.repo.AuthRepository
import com.example.examine.data.repo.TestRepository
import com.example.examine.ui.main.home.HomeViewModel
import com.example.examine.ui.main.profile.ProfileViewModel
import com.example.examine.utils.UserPreferences

class TestViewModelFactory constructor(
    private val apiService: ApiService,
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(TestViewModel::class.java)) {
            TestViewModel(TestRepository(apiService)) as T
        } else
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}