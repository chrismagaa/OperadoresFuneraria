package com.pabs.operadores_funeraria.ui.main

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pabs.operadores_funeraria.core.ScarletHelper
import com.pabs.operadores_funeraria.data.network.EchoService
import com.pabs.operadores_funeraria.data.network.model.User
import com.pabs.operadores_funeraria.utils.Session
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory

class MainViewModel : ViewModel() {


    val user = MutableLiveData<User>()

    init {
        user.postValue(Session.instance.user)
    }

    fun logout(context: Context, onLogOut: () -> Unit) {
        Session.instance.logout(context, onLogOut)
    }

    fun updateUser(context: Context,user : User){
        Session.instance.updateUser(context, user)
        this.user.postValue(user)
    }















}