package com.pabs.operadores_funeraria.data.network

import android.util.Log
import com.pabs.operadores_funeraria.BuildConfig
import com.pabs.operadores_funeraria.core.Interceptor
import com.pabs.operadores_funeraria.core.RetrofitHelper
import com.pabs.operadores_funeraria.data.network.model.FinalizarRecoResponse
import com.pabs.operadores_funeraria.data.network.model.LoginResponse
import com.pabs.operadores_funeraria.data.network.model.ServicioResponse
import com.pabs.operadores_funeraria.data.network.model.VersionAppResponse
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
            try{
                val response = retrofit.create(ApiService::class.java).login(userName, password)
                response.body()
            }catch (e: Interceptor.NoInternetException){
                null
            }

        }
    }

    suspend fun getServicio(
        id: Int
    ): ServicioResponse? {
        if(BuildConfig.DEBUG){
            Log.d(tag, "getServicio()")
        }
        return withContext(Dispatchers.IO){
            try{
                val response = retrofit.create(ApiService::class.java).servicio(id)
                response.body()
            }catch (e: Interceptor.NoInternetException){
                null
            }

        }
    }

    suspend fun getVersionApp(): VersionAppResponse? {
        if(BuildConfig.DEBUG){
            Log.d(tag, "getVersionApp()")
        }
        return withContext(Dispatchers.IO){
            try{
                val response = retrofit.create(ApiService::class.java).getVersionApp()
                response.body()
            }catch (e: Interceptor.NoInternetException) {
                null
            }
        }
    }


    suspend fun finalizarReco(
        idUser: Int,
        idServicio: Int,
        code: String
    ): FinalizarRecoResponse? {
        if(BuildConfig.DEBUG){
            Log.d(tag, "finalizarReco()")
        }
        return withContext(Dispatchers.IO){
            try{
                val response = retrofit.create(ApiService::class.java).finalizarReco(idUser,idServicio, code)
                response.body()
            }catch (e: Interceptor.NoInternetException){
                null
            }

        }
    }


}