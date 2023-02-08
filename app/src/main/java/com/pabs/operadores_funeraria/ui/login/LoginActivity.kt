package com.pabs.operadores_funeraria.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.viewbinding.BuildConfig
import com.pabs.operadores_funeraria.data.network.model.LoginResponse
import com.pabs.operadores_funeraria.databinding.ActivityLoginBinding
import com.pabs.operadores_funeraria.ui.main.MainActivity
import com.pabs.operadores_funeraria.utils.Session

class LoginActivity : AppCompatActivity() {

    private val tag = "LoginActivity"

    //binding
    private lateinit var binding: ActivityLoginBinding

    //init viemodel
    private val vmLogin: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "onCreate()")
        }
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        vmLogin.loginResponse.observe(this) {
            if (BuildConfig.DEBUG) {
                Log.d(tag, it.toString())
            }

            it?.let { response ->
                if (response.status_code == "200" && response.user != null) {
                    Session.instance.update(this, response)
                    if (response.user.role == "OPERATIVO") {
                        goToHome()
                    }
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }

            }
        }


        binding.btnLogin.setOnClickListener {
            login(binding.etUserName.text.toString(), binding.etPassword.text.toString())
        }
    }

    private fun goToHome() {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "goToHome()")
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun login(userName: String, password: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "login()")
        }
        vmLogin.login(userName, password)
    }


}