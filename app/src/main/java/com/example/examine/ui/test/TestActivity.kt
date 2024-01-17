package com.example.examine.ui.test

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.examine.R
import com.example.examine.data.remote.models.AnswerPayload
import com.example.examine.data.remote.models.TestQuestionsItem
import com.example.examine.data.remote.models.TestSession
import com.example.examine.databinding.ActivityTestBinding
import com.example.examine.simpleinjection.Injection
import com.example.examine.ui.adapter.TestQuestionsAdapter
import com.example.examine.utils.Constants.alertDialogMessage
import com.example.examine.utils.Result


@BuildCompat.PrereleaseSdkCheck
class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding
    private var testSession: TestSession? = null

    private val testQuestionsAdapter = TestQuestionsAdapter()

    private val testCode by lazy {
        intent.getStringExtra(
            KEY_TEST_CODE
        )
    }

    private val testViewModel by viewModels<TestViewModel> {
        TestViewModelFactory(
            Injection.provideApiService(this)
        )
    }

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(this)
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        testSession = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(KEY_TEST, TestSession::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(KEY_TEST)
        }

        setClosingStopper()

        setTestRecyclerView()
        setListeners()
    }

    private fun setClosingStopper() {
        if (BuildCompat.isAtLeastT()) {
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                alertDialogMessage(
                    this@TestActivity,
                    "Selesaikan Tes terlebih dahulu!",
                    "Peringatan"
                )
            }
        } else {
            onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    alertDialogMessage(
                        this@TestActivity,
                        "Selesaikan Tes terlebih dahulu!",
                        "Peringatan"
                    )
                }
            })
        }
    }

    private fun setTestRecyclerView() {
        binding.apply {
            toolbar.title = testSession?.test?.subject.toString()

            rvQuestions.apply {
                testQuestionsAdapter.setList(testSession?.test?.testQuestions as ArrayList<TestQuestionsItem>)
                adapter = testQuestionsAdapter
                layoutManager = LinearLayoutManager(this@TestActivity)
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnSubmit.setOnClickListener {
                if (!checkAnswers()) {
                    alertDialogMessage(this@TestActivity, "Selesaikan semua soal terlebih dahulu!")
                } else {
                    val builder = AlertDialog.Builder(this@TestActivity)
                    builder.setCancelable(false)

                    with(builder)
                    {
                        setTitle("Submit Tes")
                        setMessage("Apakah anda sudah yakin dengan jawaban tes anda?")
                        setPositiveButton("Ya") { _, _ ->
                            submitTest()
                        }
                        setNegativeButton("Belum") { _, _ ->

                        }
                        show()
                    }
                }
            }
        }
    }

    private fun submitTest() {
        val answerPayload = AnswerPayload(
            code = testCode.toString(),
            answers = testQuestionsAdapter.getAllAnswer()
        )

        testViewModel.storeTest(answerPayload).observe(this@TestActivity) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog.show()
                }

                is Result.Success -> {
                    loadingDialog.dismiss()
                    val builder = AlertDialog.Builder(this@TestActivity)
                    builder.setCancelable(false)

                    with(builder)
                    {
                        setTitle("Berhasil Submit Tes")
                        setMessage("Klik OK untuk kembali ke halaman utama.")
                        setPositiveButton("OK") { _, _ ->
                            finish()
                        }
                        show()
                    }
                }

                is Result.Error -> {
                    loadingDialog.dismiss()
                    alertDialogMessage(this@TestActivity, result.error, "Gagal Submit Tes")
                }
            }
        }
    }

    private fun checkAnswers(): Boolean {
        var flag = true
        for (answer in testQuestionsAdapter.getAllAnswer()) {
            if (answer.answer.isEmpty()) {
                flag = false
                break
            }
        }

        return flag
    }

    override fun onPause() {
        alertDialogMessage(this, "Anda terdeteksi keluar dari aplikasi!", "Peringatan")
        super.onPause()
    }

    companion object {
        const val KEY_TEST = "key_test"
        const val KEY_TEST_CODE = "key_test_code"
    }
}