package com.example.examine

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.example.examine.utils.DeviceIDHelper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

class ScanQRCodesTest {
    private lateinit var context: Context
    private lateinit var activity: Activity
    private lateinit var contentResolver: ContentResolver
    private lateinit var telephonyManager: TelephonyManager

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        activity = mock(Activity::class.java)
        contentResolver = mock(ContentResolver::class.java)
        telephonyManager = mock(TelephonyManager::class.java)

        `when`(context.contentResolver).thenReturn(contentResolver)
    }

    @Test
    fun `Test Scan QR Code when Device ID Valid - Success`() {
        `when`(context.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager)
        `when`(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE))
            .thenReturn(PackageManager.PERMISSION_GRANTED)
        `when`(telephonyManager.imei).thenReturn("123456789012345")

        val deviceID = DeviceIDHelper.getDeviceID(context, activity)
        assertEquals("123456789012345", deviceID)
    }

    @Test
    fun `Test Scan QR Code when Device ID Invalid - Failed`() {
        `when`(context.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(telephonyManager)
        `when`(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE))
            .thenReturn(PackageManager.PERMISSION_GRANTED)
        `when`(telephonyManager.imei).thenReturn(null)

        mockStatic(Settings.Secure::class.java).use { mockedSettings ->
            mockedSettings.`when`<String> {
                Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            }.thenReturn("android_id_67890")

            val deviceID = DeviceIDHelper.getDeviceID(context, activity)
            assertEquals("android_id_67890", deviceID)
        }
    }
}
