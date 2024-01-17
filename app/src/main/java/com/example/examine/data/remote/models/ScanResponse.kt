package com.example.examine.data.remote.models

import com.google.gson.annotations.SerializedName

data class ScanResponse(
    @SerializedName("iv")
    val iv: String,

    @SerializedName("value")
    val value: String,

    @SerializedName("mac")
    val mac: String,

    @SerializedName("tag")
    val tag: String
)