package com.example.examine

import android.app.Activity
import android.content.Context
import android.util.Base64
import com.example.examine.data.remote.models.ScanResponse
import com.example.examine.ui.main.home.HomeViewModel
import com.example.examine.utils.DeviceIDHelper
import com.example.examine.utils.ScanQRCodeProcessor
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.ExpectedException

class ScanQrCodeTest {
    private lateinit var mockBase64: Base64Mock

    private lateinit var scanQrCodeProcessor: ScanQRCodeProcessor
    private val context: Context = mock(Context::class.java)
    private val activity: Activity = mock(Activity::class.java)
    private val deviceIDHelper: DeviceIDHelper = mock(DeviceIDHelper::class.java)
    private val homeViewModel: HomeViewModel = mock(HomeViewModel::class.java)

    @get:Rule
    val exceptionRule: ExpectedException = ExpectedException.none()

    @Before
    fun setUp() {
        mockStatic(android.util.Base64::class.java)
        mockBase64 = Base64Mock

        // When
        `when`(Base64.decode(any(String::class.java), eq(Base64.NO_WRAP)))
            .thenAnswer { invocation ->
                mockBase64.decode(invocation.arguments[0] as String, Base64.NO_WRAP)
            }

        `when`(Base64.encodeToString(any(ByteArray::class.java), eq(Base64.NO_WRAP)))
            .thenAnswer { invocation ->
                mockBase64.encodeToString(invocation.arguments[0] as ByteArray, Base64.NO_WRAP)
            }

        scanQrCodeProcessor = ScanQRCodeProcessor(context, activity, deviceIDHelper, homeViewModel)
    }

    @Test
    fun `Test Scan QR Code when Device ID Valid - Success`() {
        // Arrange
        val encryptedSessionCode = "encryptedSessionCode"
        val decryptedSessionCode = ScanResponse(value = "decryptedSessionCode")
        `when`(scanQrCodeProcessor.decryptCode(encryptedSessionCode)).thenReturn(decryptedSessionCode)
        `when`(deviceIDHelper.getDeviceID(any(), any())).thenReturn("VALID_IMEI")
        `when`(homeViewModel.getImei).thenReturn("VALID_IMEI")

        // Act
        scanQrCodeProcessor.processScannedQrCode(encryptedSessionCode)

        // Assert
        verify(scanQrCodeProcessor).startPresent(any())
    }

    @Test
    fun `Test Scan QR Code when Device ID Invalid - Failed`() {
        // Arrange
        val encryptedSessionCode = "validEncryptedCode"
        `when`(deviceIDHelper.getDeviceID(context, activity)).thenReturn("INVALID_IMEI")
        `when`(homeViewModel.getImei).thenReturn("VALID_IMEI")

        // Expect exception
        exceptionRule.expect(Error::class.java)
        exceptionRule.expectMessage("Device ID tidak Sama")

        // Act
        scanQrCodeProcessor.processScannedQrCode(encryptedSessionCode)
    }
}
