package com.example.examine.ui.main.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.examine.data.repo.TestRepository
import com.example.examine.utils.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class HomeViewModel(
    private val testRepository: TestRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    val getImei = runBlocking { userPreferences.getImei().first().toString() }
    val tempIV = MutableLiveData("")

    fun startAbsent(sessionTestCode: String, iv: String) =
        testRepository.getTestSessionScan(sessionTestCode, iv)

    fun startTest(sessionTestCode: String) =
        testRepository.startTest(sessionTestCode)
}