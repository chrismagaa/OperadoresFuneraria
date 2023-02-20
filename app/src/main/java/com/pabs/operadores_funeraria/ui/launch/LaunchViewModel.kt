package com.pabs.operadores_funeraria.ui.launch

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pabs.operadores_funeraria.BuildConfig
import com.pabs.operadores_funeraria.data.Repository
import com.pabs.operadores_funeraria.data.network.model.LoginResponse
import com.pabs.operadores_funeraria.data.network.model.VersionAppResponse
import kotlinx.coroutines.launch

class LaunchViewModel: ViewModel() {
    private val tag = "LaunchViewModel"

    val loginResponse = MutableLiveData<LoginResponse?>()

    private val repository = Repository()

    val versionApp = MutableLiveData<VersionAppResponse?>()

    fun getVersionApp(){
        if (BuildConfig.DEBUG) {
            Log.d(tag, "getVersionApp()")
        }
        viewModelScope.launch {
            val response = repository.getVersionApp()
            if(response?.status_code == "200"){
                    versionApp.postValue(response)
            }else{
                versionApp.postValue(null)
            }
        }
    }


}