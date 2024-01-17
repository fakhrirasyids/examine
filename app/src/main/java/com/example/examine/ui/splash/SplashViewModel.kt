package com.example.examine.ui.splash

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.examine.data.repo.AuthRepository
import com.example.examine.utils.UserPreferences
import kotlinx.coroutines.launch

class SplashViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {
    fun getAccessToken() = userPreferences.getAccessToken().asLiveData()
}