package com.example.examine.utils

import com.example.examine.BuildConfig
import com.example.examine.data.remote.models.ScanResponse
import com.google.gson.Gson
import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CipherHelper {
    fun base64Decoder(base64Code: String): ByteArray {
        return Base64.decode(base64Code, Base64.NO_WRAP)
    }

    private fun base64Encoder(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    fun jsonToScanResponse(jsonResponse: String): ScanResponse =
        Gson().fromJson(jsonResponse, ScanResponse::class.java)

    fun provideTrueInitializeValue(ivTrue: ByteArray) = IvParameterSpec(ivTrue)

    fun encryptTestSessionCode(
        plainText: String,
        iv: IvParameterSpec,
        encryptionKey: String = BuildConfig.ENCRYPTION_KEY
    ): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(base64Decoder(encryptionKey), "AES"),
            iv
        )
        val cipherText = cipher.doFinal(plainText.toByteArray())
        return base64Encoder(cipherText)
    }

    fun decryptTestSessionCode(
        cipherText: String,
        iv: IvParameterSpec,
        encryptionKey: String = BuildConfig.ENCRYPTION_KEY
    ): String {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(base64Decoder(encryptionKey), "AES"),
            iv
        )
        val plainText = cipher.doFinal(base64Decoder(cipherText))
        println(base64Decoder(cipherText))
        return String(plainText)
    }
}
