package com.example.examine.data.remote.models

import com.google.gson.annotations.SerializedName

data class StoreTestResponse(
    @field:SerializedName("success")
    val success: Boolean? = null,

    @field:SerializedName("message")
    val message: String? = null
)