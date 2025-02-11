package com.example.examine

import android.annotation.SuppressLint
import android.util.Base64
import com.example.examine.utils.CipherHelper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import java.security.SecureRandom
import javax.crypto.spec.IvParameterSpec

class CipherHelperTest {
    private lateinit var mockBase64: Base64Mock
    private lateinit var cipherHelper: CipherHelper

    @SuppressLint("CheckResult")
    @Before
    fun setup() {
        mockStatic(android.util.Base64::class.java)
        mockBase64 = Base64Mock
        cipherHelper = CipherHelper
    }

    @Test
    fun `Test AES Encrypt & Decrypt`() {
        // Given
        val encryptionKey = "iQAD5O7mVr90ZxLuqbnaN32yDg8RNPcxyK293GbZfZY="
        val plainText = "This is a test message."

        val ivBytes = ByteArray(16)
        SecureRandom().nextBytes(ivBytes)
        val iv = IvParameterSpec(ivBytes)

        // When
        `when`(Base64.decode(any(String::class.java), eq(Base64.NO_WRAP)))
            .thenAnswer { invocation ->
                mockBase64.decode(invocation.arguments[0] as String, Base64.NO_WRAP)
            }

        `when`(Base64.encodeToString(any(ByteArray::class.java), eq(Base64.NO_WRAP)))
            .thenAnswer { invocation ->
                mockBase64.encodeToString(invocation.arguments[0] as ByteArray, Base64.NO_WRAP)
            }

        val encryptedText = cipherHelper.encryptTestSessionCode(plainText, iv, encryptionKey)
        val decryptedText = cipherHelper.decryptTestSessionCode(encryptedText, iv, encryptionKey)

        // Then
        assertEquals(plainText, decryptedText)
    }
}

object Base64Mock {
    @JvmStatic
    fun encodeToString(input: ByteArray?, flags: Int): String {
        return java.util.Base64.getEncoder().encodeToString(input)
    }

    @JvmStatic
    fun decode(str: String?, flags: Int): ByteArray {
        return java.util.Base64.getDecoder().decode(str)
    }
}
