package com.pabs.operadores_funeraria.data.network.model

data class LoginResponse (
    val status_code: String,
    val auth_token: String,
    val id: Int,
    val username: String,
    val role: String)