package com.example.examine.utils

import android.app.Activity
import android.content.Context
import com.example.examine.data.remote.models.ScanResponse
import com.example.examine.ui.main.home.HomeViewModel
import com.example.examine.utils.Constants.alertDialogMessage
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class ScanQRCodeProcessor(
    private val context: Context,
    private val activity: Activity,
    private val deviceIdHelper: DeviceIDHelper,
    private val homeViewModel: HomeViewModel,
) {
    fun processScannedQrCode(encryptedSessionCode: String) {
        val decryptedSessionCode = decryptCode(encryptedSessionCode)
        if (!checkIsDeviceIDSame()) throw Error("Device ID tidak Sama")
        val encryptedUserMetadata = constructUserMetadata(decryptedSessionCode)
        startPresent(encryptedUserMetadata)
    }

    fun constructUserMetadata(decryptedSessionCode: ScanResponse): ScanResponse {
        val currentDate =
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())
        val currentTime =
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(System.currentTimeMillis())
        val jsonRequestBodyPresent = JSONObject()
        jsonRequestBodyPresent.put("code", decryptedSessionCode.value)
        jsonRequestBodyPresent.put("imei", homeViewModel.getImei)
        jsonRequestBodyPresent.put("dateFromMobile", currentDate)
        jsonRequestBodyPresent.put("timeFromMobile", currentTime)

        val jsonStringPresent = jsonRequestBodyPresent.toString()

        val ivTrueJson =
            CipherHelper.provideTrueInitializeValue(Base64Helper.base64Decoder(decryptedSessionCode.iv))
        val cipherTextRequestBody =
            CipherHelper.encryptTestSessionCode(jsonStringPresent, ivTrueJson)
        val scanBody = ScanResponse(
            value = cipherTextRequestBody,
            iv = decryptedSessionCode.iv
        )

        return scanBody
    }

    fun startPresent(scanResponse: ScanResponse) {
//        homeViewModel.startAbsent(scanResponse.value, scanResponse.iv)
//            .observe(viewLifecycleOwner) { result ->
//                when (result) {
//                    is Result.Loading -> {
//                        loadingDialog.show()
//                    }
//
//                    is Result.Success -> {
//                        loadingDialog.dismiss()
//                        alertDialogMessage(
//                            requireContext(),
//                            result.data.message.toString(),
//                            "Sukses"
//                        )
//                    }
//
//                    is Result.Error -> {
//                        loadingDialog.dismiss()
//                        alertDialogMessage(
//                            requireContext(),
//                            result.error,
//                            "Gagal Presensi"
//                        )
//                    }
//                }
//            }
    }

    fun decryptCode(encryptedSessionCode: String): ScanResponse {
//        val jsonDecoded = String(Base64Helper.base64Decoder(encryptedSessionCode))
        val scanResponseData = CipherHelper.jsonToScanResponse(encryptedSessionCode)

        val cipherText = scanResponseData.value
        val ivTrue =
            CipherHelper.provideTrueInitializeValue(Base64Helper.base64Decoder(scanResponseData.iv))
        val testSessionCode = CipherHelper.decryptTestSessionCode(cipherText, ivTrue)

        scanResponseData.value = testSessionCode

        return scanResponseData
    }

    fun checkIsDeviceIDSame(): Boolean {
        val currentPhoneImei =
            DeviceIDHelper.getDeviceID(context, activity)
        val accountImei = homeViewModel.getImei

        return currentPhoneImei != accountImei
    }
}