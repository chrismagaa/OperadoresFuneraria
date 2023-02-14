package com.pabs.operadores_funeraria.data.network.model

import com.google.gson.GsonBuilder

data class ServicioResponse(
    val status_code: String? = "",
    val servicio: ServicioFuneral
    )
