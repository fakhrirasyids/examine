package com.example.examine.data.repo

import androidx.lifecycle.liveData
import com.example.examine.data.remote.ApiService
import com.example.examine.utils.Result

class AuthRepository(
    private val apiService: ApiService
) {
    fun loginUser(username: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val loginUser = apiService.loginUser(username, password)
            emit(Result.Success(loginUser))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun registerUser(
        fullname: String,
        nisn: String,
        email: String,
        password: String,
    ) = liveData {
        emit(Result.Loading)
        try {
            val registerUser = apiService.registerUser(fullname, nisn, email, password)
            emit(Result.Success(registerUser))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }

    fun logoutUser() = liveData {
        emit(Result.Loading)
        try {
            val logoutResponse = apiService.logoutUser()
            emit(Result.Success(logoutResponse))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Result.Error(e.message.toString()))
        }
    }
}