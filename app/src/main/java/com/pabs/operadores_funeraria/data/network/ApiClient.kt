package com.pabs.operadores_funeraria.data.network

import android.util.Log
import com.pabs.operadores_funeraria.BuildConfig
import com.pabs.operadores_funeraria.core.RetrofitHelper
import com.pabs.operadores_funeraria.data.network.model.LoginResponse
import com.pabs.operadores_funeraria.data.network.model.ServicioResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiClient {

    private val tag = "ApiClient"
    private val retrofit = RetrofitHelper.getRetrofit()

    suspend fun login(
        userName: String,
        password: String
    ): LoginResponse? {
        if(BuildConfig.DEBUG){
            Log.d(tag, "login()")
        }
        return withContext(Dispatchers.IO){
            val response = retrofit.create(ApiService::class.java).login(userName, password)
            response.body()
        }
    }

    suspend fun getServicio(
        id: Int
    ): ServicioResponse? {
        if(BuildConfig.DEBUG){
            Log.d(tag, "getServicio()")
        }
        return withContext(Dispatchers.IO){
            val response = retrofit.create(ApiService::class.java).servicio(id)
            response.body()
        }
    }


}