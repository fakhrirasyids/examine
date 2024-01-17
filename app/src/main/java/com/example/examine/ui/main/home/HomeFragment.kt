package com.example.examine.ui.main.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.RestrictionsManager
import android.os.Build
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
import com.example.examine.data.remote.models.TestSession
import com.example.examine.databinding.FragmentHomeBinding
import com.example.examine.simpleinjection.Injection
import com.example.examine.ui.auth.AuthActivity
import com.example.examine.ui.main.MainViewModelFactory
import com.example.examine.ui.main.profile.ProfileViewModel
import com.example.examine.ui.scanner.ScannerActivity
import com.example.examine.ui.test.TestActivity
import com.example.examine.ui.test.TestActivity.Companion.KEY_TEST
import com.example.examine.ui.test.TestActivity.Companion.KEY_TEST_CODE
import com.example.examine.utils.CipherHelper
import com.example.examine.utils.Constants.alertDialogMessage
import com.example.examine.utils.Result


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

                if (urlQrCode != null) {
                    processScannedQrCode(urlQrCode)
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

    private fun processScannedQrCode(code: String) {
        try {
            loadingDialog.show()
            var testSessionCode = ""

            CipherHelper.apply {
                val jsonDecoded = String(base64Decoder(code))
                val scannedData = jsonParserQRCode(jsonDecoded)

                val cipherText = scannedData.value
                val ivTrue = provideTrueInitializeValue(base64Decoder(scannedData.iv))

                Log.i("ScannedQRCodeInfo", "Code: $code")
                Log.i("ScannedQRCodeInfo", "JSON Parsed: $jsonDecoded")
                Log.i("ScannedQRCodeInfo", "JSON Transformed: $scannedData")
                Log.i("ScannedQRCodeInfo", "Cipher Text: $cipherText")
                Log.i("ScannedQRCodeInfo", "Initialize Value: $ivTrue")

                testSessionCode = descryptTestSessionCode(cipherText, ivTrue)

                Log.i("ScannedQRCodeInfo", "Decrypted Value: $testSessionCode")
            }

            val regexPattern = Regex("s:\\d+:\"([^\"]*)\";")
            val matchResult = regexPattern.find(testSessionCode)

            val extractedValue = matchResult?.groups?.get(1)?.value

            homeViewModel.getTestScanResult(extractedValue.toString())
                .observe(viewLifecycleOwner) { result ->
                    when (result) {
                        is Result.Loading -> {
                            loadingDialog.show()
                        }

                        is Result.Success -> {
                            loadingDialog.dismiss()
                            val testResponse = result.data.payload

                            val builder = AlertDialog.Builder(requireContext())
                            builder.setCancelable(false)

                            val message = """   
Kelas: ${testResponse?.jsonMemberClass}
Guru: ${testResponse?.teacher}                                     
                                  
Subjek Tes: ${testResponse?.test?.subject}
Deskripsi: ${testResponse?.test?.description}

Waktu Mulai: ${testResponse?.timeStart}
Waktu Selesai: ${testResponse?.timeEnd}
        
Apakah anda ingin memulai Tes ini?
        """

                            with(builder)
                            {
                                setTitle("Informasi Test Didapatkan")
                                setMessage(message)
                                setPositiveButton("Mulai") { _, _ ->
                                    startTest(extractedValue.toString(), testResponse!!)
                                }
                                setNegativeButton("Batal") { _, _ ->

                                }
                                show()
                            }
                        }

                        is Result.Error -> {
                            loadingDialog.dismiss()
                            alertDialogMessage(
                                requireContext(),
                                result.error,
                                "Gagal Mendapatkan Informasi Test"
                            )
                        }
                    }
                }
        } catch (e: Exception) {
            loadingDialog.dismiss()

        }
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
            "<ol><li> Setiap Tes hanya bisa diikuti <b>1 kali</b>.</li><li> Sistem akan memonitor peserta selama tes.</li><li> Selama Tes peserta tidak diperkenankan untuk membuka aplikasi lain, jika peserta keluar dari aplikasi maka aktivitas akan terdeteksi.</li></ol>"
        binding.tvTestRules.text = Html.fromHtml(rules, Html.FROM_HTML_MODE_LEGACY)
    }

    companion object {
        const val URL_QR_CODE = "url_qr_code"
    }
}