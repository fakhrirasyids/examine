package com.example.examine.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import com.example.examine.R
import com.example.examine.databinding.ActivitySplashBinding
import com.example.examine.simpleinjection.Injection
import com.example.examine.ui.auth.AuthActivity
import com.example.examine.ui.main.MainActivity
import com.example.examine.utils.UserPreferences.Companion.preferenceDefaultValue

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private val splashViewModel by viewModels<SplashViewModel> {
        SplashViewModelFactory(
            Injection.provideUserPreferences(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            splashViewModel.getAccessToken().observe(this) { accessToken ->
                if (accessToken == preferenceDefaultValue) {
                    finish()
                    startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
                } else {
                    finish()
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                }
            }
        }, SPLASH_TIMEOUT)
    }

    companion object {
        const val SPLASH_TIMEOUT = 1800L
    }
}