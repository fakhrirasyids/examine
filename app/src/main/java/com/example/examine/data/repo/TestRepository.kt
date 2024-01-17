package com.example.examine.data.repo

import androidx.lifecycle.liveData
import com.example.examine.data.remote.ApiService
import com.example.examine.data.remote.models.AnswerPayload
import com.example.examine.utils.Result

class TestRepository(private val apiService: ApiService) {
    fun getTestSessionScan(code: String) = liveData {
        emit(Result.Loading)
        try {
            val getTestSessionResponse = apiService.getTestSessionScan(code)
            emit(Result.Success(getTestSessionResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun startTest(code: String) = liveData {
        emit(Result.Loading)
        try {
            val startTestResponse = apiService.startTest(code)
            emit(Result.Success(startTestResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun storeTest(answerPayload: AnswerPayload) = liveData {
        emit(Result.Loading)
        try {
            val storeTestResponse = apiService.storeTest(answerPayload)
            emit(Result.Success(storeTestResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }
}