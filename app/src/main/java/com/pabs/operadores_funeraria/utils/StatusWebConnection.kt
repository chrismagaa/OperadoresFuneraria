package com.pabs.operadores_funeraria.utils

import com.pabs.operadores_funeraria.R

enum class StatusWebConnection() {
    OPENED, CLOSED, CLOSING, FAILED;

    fun description(): String {
        return when (this) {
            OPENED -> "WEB CONECTADO"
            CLOSED -> "WEB DESCONECTAD"
            CLOSING -> "WEB DESCONECTANDO..."
            FAILED -> "WEB FALLIDO"
        }
    }

    fun color(): Int {
        return when (this) {
            OPENED -> R.color.green
            CLOSED -> R.color.red
            CLOSING -> R.color.yellow
            FAILED -> R.color.red
        }
    }
}