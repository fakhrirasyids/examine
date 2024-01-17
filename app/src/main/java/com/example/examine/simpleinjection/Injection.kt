package com.example.examine.simpleinjection

import android.content.Context
import com.example.examine.data.remote.ApiConfig
import com.example.examine.data.remote.ApiService
import com.example.examine.utils.UserPreferences
import com.example.examine.utils.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserPreferences(context: Context): UserPreferences {
        return UserPreferences.getInstance(context.dataStore)
    }

    fun provideApiService(context: Context): ApiService {
        val prefs = provideUserPreferences(context)
        val accessToken = runBlocking { prefs.getAccessToken().first() }
        return ApiConfig.getApiService(accessToken)
    }
}