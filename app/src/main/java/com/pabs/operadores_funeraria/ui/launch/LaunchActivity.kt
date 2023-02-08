package com.pabs.operadores_funeraria.ui.launch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.viewbinding.BuildConfig
import com.pabs.operadores_funeraria.R
import com.pabs.operadores_funeraria.ui.login.LoginActivity
import com.pabs.operadores_funeraria.ui.main.MainActivity
import com.pabs.operadores_funeraria.utils.Session

class LaunchActivity : AppCompatActivity() {

    private val tag = "LaunchActivity"
    private val vmLaunch: LaunchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        supportActionBar?.hide()

        Session.instance.configure(this)

        if(Session.instance.user == null){
            goToLoginActivity()
        }else{
            iniciar(Session.instance.user?.role?:"")
        }

        vmLaunch.loginResponse.observe(this){
            it?.let {loginResponse ->
                if (loginResponse.status_code  == "200"){
                    Session.instance.update(this, loginResponse)
                    iniciar(loginResponse.user?.role?:"")
                }else{
                    goToLoginActivity()
                }
            }
        }
    }

    private fun iniciar(role: String) {
        if(role == "OPERATIVO"){
            goToHome()
        }
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToHome() {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "goToHome()")
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}