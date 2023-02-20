package com.pabs.operadores_funeraria.data.network.model

data class  VersionAppResponse(
    val status_code: String? = "",
    val version_code: Int? = 0,
    val version_name: String? = "",
)