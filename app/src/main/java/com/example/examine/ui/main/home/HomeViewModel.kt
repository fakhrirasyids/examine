package com.example.examine.ui.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examine.data.repo.AuthRepository
import com.example.examine.data.repo.TestRepository
import com.example.examine.utils.UserPreferences
import kotlinx.coroutines.launch

class HomeViewModel(
    private val testRepository: TestRepository
) : ViewModel() {
    fun getTestScanResult(sessionTestCode: String) =
        testRepository.getTestSessionScan(sessionTestCode)

    fun startTest(sessionTestCode: String) =
        testRepository.startTest(sessionTestCode)
}