package com.pabs.operadores_funeraria.ui.main

import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.pabs.operadores_funeraria.BuildConfig
import com.pabs.operadores_funeraria.core.ScarletHelper
import com.pabs.operadores_funeraria.data.Repository
import com.pabs.operadores_funeraria.data.network.EchoService
import com.pabs.operadores_funeraria.data.network.model.FinalizarRecoResponse
import com.pabs.operadores_funeraria.data.network.model.ServicioFuneral
import com.pabs.operadores_funeraria.data.network.model.User
import com.pabs.operadores_funeraria.common.MessageDialog
import com.pabs.operadores_funeraria.common.MessageType
import com.pabs.operadores_funeraria.common.Session
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    private val tag = "MainViewModel"

    private val repository = Repository()


    val user = MutableLiveData<User>()
    val servicio = MutableLiveData<ServicioFuneral?>()
    val distancia = MutableLiveData<Double>()
    val message = MutableLiveData<MessageDialog>()

    val finalizarReco = MutableLiveData<FinalizarRecoResponse?>()

    val gpsEnabled = MutableLiveData<Boolean>()

    val permissionEnabledLocation = MutableLiveData<Boolean>()


    fun onCreate(context: Context) {
        user.postValue(Session.instance.user)
        servicio.postValue(Session.instance.servicio)
        checkGPSEnabled(context)
    }

    private fun checkGPSEnabled(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsEnabled.postValue(false)
        } else {
            gpsEnabled.postValue(true)
        }
    }

    fun setDistancia(lat: Double, lon: Double) {
        if (servicio.value?.reco_lat != null) {
            val distance = SphericalUtil.computeDistanceBetween(
                LatLng(lat, lon),
                servicio.value?.reco_lat?.let { LatLng(it, servicio.value?.reco_lng!!) }
            );

            this.distancia.postValue(distance/1000)
        }
    }

    fun logout(context: Context, onLogOut: () -> Unit) {
        Session.instance.logout(context, onLogOut)
    }

    fun updateUser(context: Context, user: User) {
        Session.instance.updateUser(context, user)
        this.user.postValue(user)
    }

    var webSocketService: EchoService? = null


    fun setupWebSocketService(urlSocket: String) {
        webSocketService = provideWebSocketService(
            ScarletHelper.provideScarlet(
                socketUrl = urlSocket,
                client = ScarletHelper.provideOkhttp(),
                streamAdapterFactory = provideStreamAdapterFactory(),
            )
        )

    }

    private fun provideWebSocketService(scarlet: Scarlet) = scarlet.create(EchoService::class.java)
    private fun provideStreamAdapterFactory() = RxJava2StreamAdapterFactory()


    fun refreshServicio(context: Context) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "refreshServicio()")
        }
        viewModelScope.launch {
            isLoading.postValue(true)
            repository.getServicio(user.value!!.id,{nuevoServicio ->
                //OnSuccess
                //if(nuevoServicio != servicio.value){
                    Session.instance.updateServicio(context, nuevoServicio)
                    servicio.postValue(nuevoServicio)
                //}
            },{sMessageError ->
                //OnFailure
                message.postValue(MessageDialog(MessageType.ERROR, sMessageError))
            })

            isLoading.postValue(false)
        }
    }


    fun finalizarRecoleccion(context: Context, code: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, "finalizarRecolección()")
        }
        viewModelScope.launch {
            isLoading.postValue(true)
            repository.finalizarReco(user.value!!.id,servicio.value!!.id_servicio!!, code,{respuesta ->
                //OnSuccess
                finalizarReco.postValue(respuesta)
                //Refrescamos el servicio
                refreshServicio(context)

            },{
                //OnFailure
                message.postValue(MessageDialog(MessageType.ERROR, it))
            })

            isLoading.postValue(false)
        }
    }


}