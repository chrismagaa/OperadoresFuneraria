package com.pabs.operadores_funeraria.ui.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.pabs.operadores_funeraria.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    //binding
    private lateinit var binding: ActivityLoginBinding

    //init viemodel
    private val vmLogin: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        binding.btnLogin.setOnClickListener {
            val userName = binding.etUserName.text.toString()
            val password = binding.etPassword.text.toString()
            vmLogin.login(userName, password)
        }


        setContentView(binding.root)
    }


}