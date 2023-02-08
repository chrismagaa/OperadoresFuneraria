package com.pabs.operadores_funeraria.data

import android.util.Log
import androidx.viewbinding.BuildConfig
import com.pabs.operadores_funeraria.data.network.ApiClient
import com.pabs.operadores_funeraria.data.network.model.LoginResponse

class Repository {
    private val tag = "Repository"
    private val api = ApiClient()


    suspend fun login(username: String, password: String): LoginResponse? {
        if(BuildConfig.DEBUG){
            Log.d(tag, "login()")
        }
        return api.login(username, password)
    }
}