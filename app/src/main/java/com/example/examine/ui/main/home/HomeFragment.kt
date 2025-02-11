package com.example.examine.ui.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.RestrictionsManager
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.example.examine.R
import com.example.examine.data.remote.models.ScanResponse
import com.example.examine.data.remote.models.TestSession
import com.example.examine.databinding.FragmentHomeBinding
import com.example.examine.simpleinjection.Injection
import com.example.examine.ui.main.MainViewModelFactory
import com.example.examine.ui.scanner.ScannerActivity
import com.example.examine.ui.test.TestActivity
import com.example.examine.ui.test.TestActivity.Companion.KEY_TEST
import com.example.examine.ui.test.TestActivity.Companion.KEY_TEST_CODE
import com.example.examine.utils.Base64Helper
import com.example.examine.utils.CipherHelper
import com.example.examine.utils.Constants.alertDialogMessage
import com.example.examine.utils.DeviceIDHelper
import com.example.examine.utils.Result
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var loadingDialog: AlertDialog

    private val homeViewModel by viewModels<HomeViewModel> {
        MainViewModelFactory(
            Injection.provideApiService(requireContext()),
            Injection.provideUserPreferences(requireContext())
        )
    }

    private val scannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            if (result.data != null) {
                val urlQrCode = result.data!!.getStringExtra(URL_QR_CODE)
                homeViewModel.tempIV.postValue("")

                try {
                    if (urlQrCode != null) {
                        processScannedQrCode(urlQrCode)
                    }
                } catch (e: Exception) {
                    loadingDialog.dismiss()

                    alertDialogMessage(
                        requireContext(),
                        e.message.toString(),
                        "Gagal"
                    )
                }
            }
        } else if (result.resultCode == RestrictionsManager.RESULT_ERROR) {
            alertDialogMessage(requireContext(), "Gagal Scan!")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(requireContext())
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        setRules()
        setListeners()

        return binding.root
    }

    private fun setListeners() {
        binding.apply {
            btnScanner.setOnClickListener {
                val iScan = Intent(requireActivity(), ScannerActivity::class.java)
                scannerLauncher.launch(iScan)
            }
        }
    }

    private fun processScannedQrCode(encryptedSessionCode: String) {
        val decryptedSessionCode = decryptCode(encryptedSessionCode)
        if (!checkIsDeviceIDSame()) throw IllegalArgumentException("Device ID tidak Sama")
        val encryptedUserMetadata = constructUserMetadata(decryptedSessionCode)
        startPresent(encryptedUserMetadata)
    }

    private fun constructUserMetadata(decryptedSessionCode: ScanResponse): ScanResponse {
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

    private fun startPresent(scanResponse: ScanResponse) {
        homeViewModel.startAbsent(scanResponse.value, scanResponse.iv)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        loadingDialog.show()
                    }

                    is Result.Success -> {
                        loadingDialog.dismiss()
                        alertDialogMessage(
                            requireContext(),
                            result.data.message.toString(),
                            "Sukses"
                        )
                    }

                    is Result.Error -> {
                        loadingDialog.dismiss()
                        alertDialogMessage(
                            requireContext(),
                            result.error,
                            "Gagal Presensi"
                        )
                    }
                }
            }
    }

    private fun decryptCode(encryptedSessionCode: String): ScanResponse {
        val jsonDecoded = String(Base64Helper.base64Decoder(encryptedSessionCode))
        val scanResponseData = CipherHelper.jsonToScanResponse(jsonDecoded)

        val cipherText = scanResponseData.value
        val ivTrue =
            CipherHelper.provideTrueInitializeValue(Base64Helper.base64Decoder(scanResponseData.iv))
        val testSessionCode = CipherHelper.decryptTestSessionCode(cipherText, ivTrue)

        scanResponseData.value = testSessionCode

        return scanResponseData
    }

    private fun checkIsDeviceIDSame(): Boolean {
        val currentPhoneImei =
            DeviceIDHelper.getDeviceID(requireContext(), requireActivity())
        val accountImei = homeViewModel.getImei

        Log.e("FTEST", "checkIsDeviceIDSame: $currentPhoneImei == $accountImei", )

        return currentPhoneImei == accountImei
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startTest(code: String, test: TestSession) {
        homeViewModel.startTest(code).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.show()
                }

                is Result.Success -> {
                    loadingDialog.dismiss()
                    if (result.data.success!!) {
                        val iTest = Intent(requireContext(), TestActivity::class.java)
                        iTest.putExtra(KEY_TEST_CODE, code)
                        iTest.putExtra(KEY_TEST, test)
                        requireActivity().startActivity(iTest)
                    } else {
                        alertDialogMessage(
                            requireContext(),
                            result.data.message.toString(),
                            "Gagal Memulai Test"
                        )
                    }
                }

                is Result.Error -> {
                    loadingDialog.dismiss()
                    alertDialogMessage(
                        requireContext(),
                        result.error,
                        "Gagal Memulai Test"
                    )
                }
            }
        }
    }

    private fun setRules() {
        val rules =
            "<ol><li> Presensi hanya bisa dilakukan di Smartphone yang <b>telah terdaftar</b>.</li><li> Setiap presensi hanya bisa dilakukan <b>1 kali</b>.</ol>"
        binding.tvTestRules.text = Html.fromHtml(rules, Html.FROM_HTML_MODE_LEGACY)
    }

    companion object {
        const val URL_QR_CODE = "url_qr_code"
    }
}