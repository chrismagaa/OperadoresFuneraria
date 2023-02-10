package com.pabs.operadores_funeraria.data.network.model

import com.google.gson.Gson

data class MyLocationService(
    val placas: String,
    val operativo: String,
    val lat: Double,
    val lng: Double
){
    companion object{
        //json to object with gson
        fun fromJson(json: String): MyLocationService{
            return Gson().fromJson(json, MyLocationService::class.java)
        }

        //object to json with gson
        fun toJson(myLocationService: MyLocationService): String{
            return Gson().toJson(myLocationService)
        }
    }
}