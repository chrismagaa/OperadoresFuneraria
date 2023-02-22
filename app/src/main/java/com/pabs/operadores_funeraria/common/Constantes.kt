package com.pabs.operadores_funeraria.common

object Constantes {
    const val API_BASE_URL = "https://private-a10d2-operativosfuneraria.apiary-mock.com"
    const val LOGIN_URL = "login"
    const val SERVICIO_URL = "servicio"
    const val VERSION_APP = "version-app"
    const val VALIDAR_FIN_RECO = "finalizar-reco"

}

enum class TipoServicio(val value: String) {
    RECOLECCION("recoleccion"),
    TRASLADO("traslado"),
    INCINERACION("incineracion"),
    CEMENTERIO("cementerio"),
    OTROS("otros")
}