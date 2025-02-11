package com.example.examine.utils

import android.util.Base64

object Base64Helper {
    fun base64Decoder(base64Code: String): ByteArray {
        return Base64.decode(base64Code, Base64.NO_WRAP)
    }

    fun base64Encoder(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }
}