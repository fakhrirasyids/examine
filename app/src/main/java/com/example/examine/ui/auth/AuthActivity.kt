package com.example.examine.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.examine.R
import com.example.examine.databinding.ActivityAuthBinding
import com.example.examine.ui.auth.login.LoginFragment

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.auth_container, LoginFragment())
                .commit()
        }
    }
}