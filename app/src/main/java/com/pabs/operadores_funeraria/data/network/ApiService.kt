package com.pabs.operadores_funeraria.data.network

import com.pabs.operadores_funeraria.data.network.model.LoginResponse
import com.pabs.operadores_funeraria.utils.Constantes.LOGIN_URL
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {


    @POST(LOGIN_URL)
    @FormUrlEncoded
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): Response<LoginResponse>

}