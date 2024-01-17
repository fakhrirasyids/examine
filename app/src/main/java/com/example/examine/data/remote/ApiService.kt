package com.example.examine.data.remote

import com.example.examine.data.remote.models.AnswerPayload
import com.example.examine.data.remote.models.GetTestSessionResponse
import com.example.examine.data.remote.models.LoginRegisterResponse
import com.example.examine.data.remote.models.LogoutResponse
import com.example.examine.data.remote.models.StartTestResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("api/login")
    @FormUrlEncoded
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginRegisterResponse

    @POST("api/register")
    @FormUrlEncoded
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("nisn") nisn: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginRegisterResponse

    @POST("logout")
    suspend fun logoutUser(
    ): LogoutResponse

    @POST("api/sessions")
    @FormUrlEncoded
    suspend fun getTestSessionScan(
        @Field("code") code: String,
    ): GetTestSessionResponse

    @POST("api/sessions/start")
    @FormUrlEncoded
    suspend fun startTest(
        @Field("code") code: String,
    ): StartTestResponse

    @POST("api/sessions/store-answer")
    suspend fun storeTest(
        @Body answerPayload: AnswerPayload,
    ): StartTestResponse
}