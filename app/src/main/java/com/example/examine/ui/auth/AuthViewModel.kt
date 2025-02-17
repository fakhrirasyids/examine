package com.example.examine.ui.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.examine.data.repo.AuthRepository
import com.example.examine.utils.UserPreferences
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val responseMessage = MutableLiveData<String>()

    val isLoginUsernameFilled = MutableLiveData(false)
    val isLoginPasswordFilled = MutableLiveData(false)

    val isRegisterUsernameFilled = MutableLiveData(false)
    val isRegisterFullnameFilled = MutableLiveData(false)
    val isRegisterEmailFilled = MutableLiveData(false)
    val isRegisterPasswordFilled = MutableLiveData<Boolean>()

    fun loginUser(username: String, password: String) =
        authRepository.loginUser(username, password)

    fun registerUser(fullname: String, imei: String, email: String, password: String) =
        authRepository.registerUser(fullname, imei, email, password)

    fun savePreferences(
        accessToken: String,
        fullname: String,
        email: String,
        imei: String
    ) {
        viewModelScope.launch {
            userPreferences.savePreferences(
                accessToken = accessToken,
                username = fullname,
                email = email,
                imei = imei
            )
        }
    }

    fun getAccessToken() = userPreferences.getAccessToken().asLiveData()
}