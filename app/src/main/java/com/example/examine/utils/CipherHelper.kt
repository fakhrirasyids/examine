package com.example.examine.utils

import com.example.examine.BuildConfig
import com.example.examine.data.remote.models.ScanResponse
import com.google.gson.Gson
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CipherHelper {
    fun base64Decoder(base64Code: String): ByteArray = Base64.getDecoder().decode(base64Code)

    fun jsonParserQRCode(jsonResponse: String): ScanResponse =
        Gson().fromJson(jsonResponse, ScanResponse::class.java)

    fun provideTrueInitializeValue(ivTrue: ByteArray) = IvParameterSpec(ivTrue)

    fun descryptTestSessionCode(cipherText: String, iv: IvParameterSpec): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(base64Decoder(BuildConfig.ENCRYPTION_KEY), "AES"),
            iv
        )
        val plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText))
        return String(plainText)
    }
}
