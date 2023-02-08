package com.pabs.operadores_funeraria.data.network.model

data class LoginResponse(
    val status_code: String? = "",
    val auth_token: String? = "",
    val user: User)