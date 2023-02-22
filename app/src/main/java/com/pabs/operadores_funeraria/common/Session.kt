package com.pabs.operadores_funeraria.common

import android.content.Context
import com.pabs.operadores_funeraria.data.network.model.LoginResponse
import com.pabs.operadores_funeraria.data.network.model.ServicioFuneral
import com.pabs.operadores_funeraria.data.network.model.User
import com.pabs.operadores_funeraria.data.persistence.PreferencesKey
import com.pabs.operadores_funeraria.data.persistence.PreferencesProvider

class Session {

    companion object{
        const val TAG = "Session"
        val instance = Session()
    }

    var token: String? = null
    var user: User? = null
    var servicio: ServicioFuneral? = null

    fun getAuthToken(): String{
        return "Bearer ${token?: ""}"
    }

    fun configure(context: Context){
        PreferencesProvider.string(context, PreferencesKey.AUTH_TOKEN)?.let {
            token = it
        }

        PreferencesProvider.string(context, PreferencesKey.USER)?.let {
            user = User.fromJson(it)
        }

        PreferencesProvider.string(context, PreferencesKey.SERVICIO)?.let {
            servicio = ServicioFuneral.fromJson(it)
        }

    }

    fun update(context: Context, loginResponse: LoginResponse){
        saveUser(context, loginResponse.user)
        saveAuthToken(context, loginResponse.auth_token!!)
        updateServicio(context, loginResponse.servicio)
    }

    private fun saveServicio(context: Context, servicio: ServicioFuneral) {
        PreferencesProvider.set(context, PreferencesKey.SERVICIO, ServicioFuneral.toJson(servicio))
        this.servicio = servicio
    }



    fun updateServicio(context: Context, servicio: ServicioFuneral?){
        if (servicio == null){
            PreferencesProvider.set(context, PreferencesKey.SERVICIO, null)
            this.servicio = null
        }else{
            saveServicio(context, servicio)
        }
    }

    fun updateUser(context: Context, user: User){
        saveUser(context, user)
    }


    fun saveUser(context: Context, user: User){
        PreferencesProvider.set(context, PreferencesKey.USER, User.toJson(user))
        this.user = user
    }

    fun saveAuthToken(context: Context, token: String){
        PreferencesProvider.set(context, PreferencesKey.AUTH_TOKEN, token)
        this.token = token
    }

    fun logout(context: Context, onLogOut: () -> Unit) {
        PreferencesProvider.clear(context)
        token = null
        user = null
        servicio = null
        onLogOut()
    }




}