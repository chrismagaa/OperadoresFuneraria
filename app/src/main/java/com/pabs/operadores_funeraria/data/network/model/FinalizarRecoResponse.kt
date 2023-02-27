package com.pabs.operadores_funeraria.data.network.model

data class FinalizarRecoResponse(
    val status_code: String,
    val is_finalized: Boolean,
    val message: String
)
