package com.example.examine.ui.main

import android.content.RestrictionsManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.example.examine.R
import com.example.examine.databinding.ActivityMainBinding
import com.example.examine.ui.main.home.HomeFragment
import com.example.examine.ui.main.profile.ProfileFragment
import com.example.examine.utils.Constants

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_container, HomeFragment())
                .commit()
        }

        setListeners()
    }

    private fun setListeners() {
        binding.apply {
            btnHome.setOnClickListener {
                supportFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.main_container,
                        HomeFragment(),
                        HomeFragment::class.java.simpleName
                    )
                    commit()
                }
            }

            btnProfile.setOnClickListener {
                supportFragmentManager.beginTransaction().apply {
                    replace(
                        R.id.main_container,
                        ProfileFragment(),
                        ProfileFragment::class.java.simpleName
                    )
                    commit()
                }
            }
        }
    }
}