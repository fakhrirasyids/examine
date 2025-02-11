package com.example.examine.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object DeviceIDHelper {
    fun getDeviceID(context: Context, activity: Activity): String {
        var deviceUniqueIdentifier: String? = null
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                deviceUniqueIdentifier = tm?.imei
            } else {
                requestPhoneStatePermission(activity)
            }
        }

        if (deviceUniqueIdentifier.isNullOrEmpty()) {
            deviceUniqueIdentifier = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }

        return deviceUniqueIdentifier ?: ""
    }

    private fun requestPhoneStatePermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_PHONE_STATE),
            REQUEST_PHONE_STATE_PERMISSION_CODE
        )
    }

    private const val REQUEST_PHONE_STATE_PERMISSION_CODE = 1001
}

