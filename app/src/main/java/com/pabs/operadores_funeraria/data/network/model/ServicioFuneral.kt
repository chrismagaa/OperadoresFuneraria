package com.pabs.operadores_funeraria.data.network.model

import com.google.gson.GsonBuilder

data class ServicioFuneral (
    val url: String? = "",
    val destino_lat: Double? = null,
    val destino_lng: Double? = null,
    val destino_name: String? = "",
) {
    companion object{
        fun toJson(servicio: ServicioFuneral): String{
            return GsonBuilder().create().toJson(servicio)
        }

        fun fromJson(json: String): ServicioFuneral{
            return GsonBuilder().create().fromJson(json, ServicioFuneral::class.java)
        }
    }
}