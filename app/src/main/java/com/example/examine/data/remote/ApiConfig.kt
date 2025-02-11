package com.example.examine.data.remote

import com.example.examine.BuildConfig
import com.example.examine.utils.UserPreferences.Companion.preferenceDefaultValue
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiConfig {
    fun getApiService(token: String): ApiService {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(
                if (token != preferenceDefaultValue) {
                    clientBuilder
                        .addInterceptor(Interceptor { chain ->
                            val req = chain.request()
                            val reqHeaders = req.newBuilder()
                                .addHeader("Authorization", "Bearer $token")
                                .build()
                            chain.proceed(reqHeaders)
                        })
                        .build()
                } else {
                    clientBuilder
                        .build()
                }
            )
            .build()
        return retrofit.create(ApiService::class.java)
    }
}