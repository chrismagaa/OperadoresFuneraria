package com.pabs.operadores_funeraria.data.network

import com.pabs.operadores_funeraria.data.network.model.FinalizarRecoResponse
import com.pabs.operadores_funeraria.data.network.model.LoginResponse
import com.pabs.operadores_funeraria.data.network.model.ServicioResponse
import com.pabs.operadores_funeraria.data.network.model.VersionAppResponse
import com.pabs.operadores_funeraria.common.Constantes.LOGIN_URL
import com.pabs.operadores_funeraria.common.Constantes.SERVICIO_URL
import com.pabs.operadores_funeraria.common.Constantes.VALIDAR_FIN_RECO
import com.pabs.operadores_funeraria.common.Constantes.VERSION_APP
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


    @GET(VALIDAR_FIN_RECO)
    suspend fun finalizarReco(
        @Query("idUser") id: Int,
        @Query("idServicio") idServicio: Int,
        @Query("code") code: String,
    ): Response<FinalizarRecoResponse>





}