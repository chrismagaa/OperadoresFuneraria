package com.pabs.operadores_funeraria.data.network

import com.pabs.operadores_funeraria.data.network.model.LoginResponse
import com.pabs.operadores_funeraria.data.network.model.ServicioResponse
import com.pabs.operadores_funeraria.data.network.model.VersionAppResponse
import com.pabs.operadores_funeraria.utils.Constantes.LOGIN_URL
import com.pabs.operadores_funeraria.utils.Constantes.SERVICIO_URL
import com.pabs.operadores_funeraria.utils.Constantes.VERSION_APP
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {


    @POST(LOGIN_URL)
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): Response<LoginResponse>


    @GET(SERVICIO_URL)
    suspend fun servicio(
        @Query("id") id: Int,
    ): Response<ServicioResponse>

    @GET(VERSION_APP)
    suspend fun getVersionApp(): Response<VersionAppResponse>


}