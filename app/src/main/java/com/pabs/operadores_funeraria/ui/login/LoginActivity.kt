package com.pabs.operadores_funeraria.ui.login

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewbinding.BuildConfig
import com.pabs.operadores_funeraria.R
import com.pabs.operadores_funeraria.databinding.ActivityLoginBinding
import com.pabs.operadores_funeraria.ui.main.MainActivity
import com.pabs.operadores_funeraria.utils.MessageFactory
import com.pabs.operadores_funeraria.utils.MessageType
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

        vmLogin.isLoading.observe(this) {
            if (it) {
                showProgressDialog()
            } else {
                hideProgressDialog()
            }
        }


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
            login()
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

    private fun login() {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "login()")
        }
        if(validateLoginDetails()) {
            vmLogin.login(binding.etUserName.text.toString(), binding.etPassword.text.toString())
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etUserName.text.toString().trim { it <= ' ' }) -> {
                MessageFactory.getSnackBar(this,MessageType.ERROR, resources.getString(R.string.err_msg_enter_user_name)).show()
                //set error in inpuyt layout
                binding.inputUserName.isErrorEnabled = true
                binding.inputUserName.error = resources.getString(R.string.err_msg_enter_user_name)


                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) ||  binding.etPassword.toString().length < 3 -> {
                MessageFactory.getSnackBar(this, MessageType.ERROR, resources.getString(R.string.err_msg_enter_password)).show()
                binding.inputPassword.isErrorEnabled = true
                binding.inputPassword.error = resources.getString(R.string.err_msg_enter_password)
                false
            }
            else -> {
                true
            }
        }
    }


    private var mProgressDialog: Dialog? = null
    fun showProgressDialog(){
        mProgressDialog = Dialog(this).apply {
            setContentView(R.layout.dialog_progress)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            window?.setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent))
        }

        if(mProgressDialog!!.isShowing.not()) mProgressDialog!!.show()
    }

    fun hideProgressDialog(){
        if(mProgressDialog != null && mProgressDialog!!.isShowing){
            mProgressDialog!!.dismiss()
        }
    }


}