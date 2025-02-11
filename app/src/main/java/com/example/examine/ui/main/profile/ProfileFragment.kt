package com.example.examine.ui.main.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.examine.utils.Result
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.example.examine.R
import com.example.examine.databinding.FragmentProfileBinding
import com.example.examine.simpleinjection.Injection.provideApiService
import com.example.examine.simpleinjection.Injection.provideUserPreferences
import com.example.examine.ui.auth.AuthActivity
import com.example.examine.ui.main.MainViewModelFactory

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val profileViewModel by viewModels<ProfileViewModel> {
        MainViewModelFactory(
            provideApiService(requireContext()),
            provideUserPreferences(requireContext())
        )
    }

    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val inflaterLoad: LayoutInflater = layoutInflater
        val loadingAlert = AlertDialog.Builder(requireContext())
            .setView(inflaterLoad.inflate(R.layout.custom_loading_dialog, null))
            .setCancelable(true)
        loadingDialog = loadingAlert.create()

        setProfileInfo()
        setListeners()

        return binding.root
    }

    private fun setProfileInfo() {
        binding.apply {
            profileViewModel.getFullname().observe(viewLifecycleOwner) {
                binding.tvFullname.text = it
            }

            profileViewModel.getEmail().observe(viewLifecycleOwner) {
                binding.tvEmail.text = it
            }
        }
    }

    private fun setListeners() {
        binding.apply {
            btnLogout.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setCancelable(false)

                with(builder)
                {
                    setMessage("Sukses Logout Akun!")
                    setPositiveButton("OK") { _, _ ->
                        profileViewModel.clearPreferences()
                        val iAuth = Intent(
                            requireActivity(),
                            AuthActivity::class.java
                        )
                        requireActivity().finishAffinity()
                        startActivity(iAuth)
                    }
                    show()
                }

//                profileViewModel.logout().observe(viewLifecycleOwner) { result ->
//                    when (result) {
//                        is Result.Loading -> {
//                            loadingDialog.show()
//                        }
//
//                        is Result.Success -> {
//                            profileViewModel.clearPreferences()
//                            loadingDialog.dismiss()
//                            val builder = AlertDialog.Builder(requireContext())
//                            builder.setCancelable(false)
//
//                            with(builder)
//                            {
//                                setMessage("Sukses Logout Akun!")
//                                setPositiveButton("OK") { _, _ ->
//                                    val iAuth = Intent(
//                                        requireActivity(),
//                                        AuthActivity::class.java
//                                    )
//                                    requireActivity().finishAffinity()
//                                    startActivity(iAuth)
//                                }
//                                show()
//                            }
//                        }
//
//                        is Result.Error -> {
//                            loadingDialog.dismiss()
//                            val builder = AlertDialog.Builder(requireContext())
//                            builder.setCancelable(false)
//
//                            with(builder)
//                            {
//                                setMessage("Gagal Logout Akun!")
//                                setPositiveButton("OK") { dialog, _ ->
//                                    dialog.dismiss()
//                                }
//                                show()
//                            }
//
//                        }
//                    }
//                }
            }
        }
    }
}