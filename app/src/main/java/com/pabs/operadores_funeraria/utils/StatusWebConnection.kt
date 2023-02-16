package com.pabs.operadores_funeraria.utils

import android.graphics.drawable.Drawable
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

    fun icon(): Int {
        return when (this) {
            OPENED -> R.drawable.point_green
            CLOSED -> R.drawable.point_red
            CLOSING -> R.drawable.point_red
            FAILED -> R.drawable.point_red
        }
    }

    fun animation(): Int? {
        return when (this) {
            OPENED -> null
            CLOSED -> R.raw.disconnected
            CLOSING -> R.raw.disconnected
            FAILED -> R.raw.disconnected
        }
    }
}