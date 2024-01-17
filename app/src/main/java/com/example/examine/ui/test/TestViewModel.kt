package com.example.examine.ui.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examine.data.remote.models.AnswerPayload
import com.example.examine.data.repo.AuthRepository
import com.example.examine.data.repo.TestRepository
import com.example.examine.utils.UserPreferences
import kotlinx.coroutines.launch

class TestViewModel(
    private val testRepository: TestRepository
) : ViewModel() {

    fun storeTest(answerPayload: AnswerPayload) =
        testRepository.storeTest(answerPayload)
}