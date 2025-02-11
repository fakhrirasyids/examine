package com.example.examine.data.remote.models

import com.google.gson.annotations.SerializedName

data class LoginRegisterResponse(

    @field:SerializedName("access_token")
    val accessToken: String? = null,

    @field:SerializedName("user")
    val user: User? = null,
)

data class User(

    @field:SerializedName("imei")
    val imei: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("email")
    val email: String? = null
)
