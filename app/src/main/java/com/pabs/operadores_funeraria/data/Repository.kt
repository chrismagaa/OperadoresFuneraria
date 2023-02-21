package com.pabs.operadores_funeraria.data

import android.util.Log
import androidx.viewbinding.BuildConfig
import com.pabs.operadores_funeraria.data.network.ApiClient
import com.pabs.operadores_funeraria.data.network.model.FinalizarRecoResponse
import com.pabs.operadores_funeraria.data.network.model.LoginResponse
import com.pabs.operadores_funeraria.data.network.model.ServicioFuneral
import com.pabs.operadores_funeraria.data.network.model.VersionAppResponse

class Repository {
    private val tag = "Repository"
    private val api = ApiClient()

    suspend fun login(username: String, password: String): LoginResponse? {
        if(BuildConfig.DEBUG){
            Log.d(tag, "login()")
        }
        return api.login(username, password)
    }

    suspend fun getServicio(id: Int): ServicioFuneral? {
        if(BuildConfig.DEBUG){
            Log.d(tag, "getServicio()")
        }

        val response = api.getServicio(id)
        return response?.servicio
    }


    suspend fun getVersionApp(): VersionAppResponse? {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "getVersionApp()")
        }

        return api.getVersionApp()
    }


    suspend fun finalizarReco(id: Int, code: String): FinalizarRecoResponse? {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "finalizarReco()")
        }

        return api.finalizarReco(id, code)
    }
}