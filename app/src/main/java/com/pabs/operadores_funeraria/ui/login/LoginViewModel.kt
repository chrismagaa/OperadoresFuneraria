package com.pabs.operadores_funeraria.ui.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabs.operadores_funeraria.BuildConfig
import com.pabs.operadores_funeraria.data.Repository
import com.pabs.operadores_funeraria.data.network.model.LoginResponse
import com.pabs.operadores_funeraria.common.MessageDialog
import com.pabs.operadores_funeraria.common.MessageType
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private val tag = "LoginViewModel"

    private val repository = Repository()
    val isLoading = MutableLiveData<Boolean>()
    val loginResponse = MutableLiveData<LoginResponse?>()
    val message = MutableLiveData<MessageDialog>()

    fun login(userName: String, password: String){
        if (BuildConfig.DEBUG) {
            Log.d(tag, "login()")
        }
        viewModelScope.launch {
            isLoading.postValue(true)
            repository.login(userName, password,{response ->
                //onSuccess
                loginResponse.postValue(response)
            },{sError ->
               //onError
                message.postValue(MessageDialog(MessageType.ERROR, sError))
            })

            isLoading.postValue(false)
        }
    }

}