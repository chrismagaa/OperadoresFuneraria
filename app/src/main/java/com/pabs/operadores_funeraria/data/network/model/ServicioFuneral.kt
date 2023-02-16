package com.pabs.operadores_funeraria.data.network.model

import com.google.gson.GsonBuilder

data class ServicioFuneral (
    val url: String? = "",
    val servicio: String? = "",
    val reco_lat: Double? = null,
    val reco_lng: Double? = null,
    val reco_name: String? = "",
    val reco_address: String? = "",
    val telefono: String? = "",
    val cliente: String? = "",
    val tipo_cliente: String? = "",
    val plan: String? = "", ) {
    companion object{
        fun toJson(servicio: ServicioFuneral): String{
            return GsonBuilder().create().toJson(servicio)
        }

        fun fromJson(json: String): ServicioFuneral{
            return GsonBuilder().create().fromJson(json, ServicioFuneral::class.java)
        }
    }
}