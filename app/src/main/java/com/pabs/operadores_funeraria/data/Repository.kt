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

    suspend fun login(username: String, password: String,  onSuccess: (LoginResponse?) -> Unit, onFailure: (String)-> Unit){
        if(BuildConfig.DEBUG){
            Log.d(tag, "login()")
        }
        val response = api.login(username, password)
        if (response == null) {
            onFailure("Revisa tu conexión a internet")
        } else if (response.status_code != "200") {
            onFailure("Usuario o contraseña incorrectos")
        } else {
            onSuccess(response)
        }

    }

    suspend fun getServicio(id: Int, onSuccess: (ServicioFuneral?) -> Unit, onFailure: (String) -> Unit) {
        if(BuildConfig.DEBUG){
            Log.d(tag, "getServicio()")
        }

        val response = api.getServicio(id)
        if(response == null){
            onFailure("Revisa tu conexión a internet")
        }else if(response.status_code != "200"){
            onFailure(response.status_code!!)
        }else{
            onSuccess(response.servicio)
        }
    }


    suspend fun getVersionApp(): VersionAppResponse? {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "getVersionApp()")
        }

        return api.getVersionApp()
    }


    suspend fun finalizarReco(idUser: Int, idServicio: Int, code: String,  onSuccess: (FinalizarRecoResponse?) -> Unit, onFailure: (String) -> Unit) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "finalizarReco()")
        }

        val response = api.finalizarReco(idUser, idServicio, code)
        if(response == null){
            onFailure("Revisa tu conexión a internet")
        }else if(response.status_code != "200"){
            onFailure(response.status_code)
        }else{
            onSuccess(response)
        }

    }
}