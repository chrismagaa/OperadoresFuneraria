package com.pabs.operadores_funeraria.utils

import com.pabs.operadores_funeraria.R

enum class StatusGPSConnection {
        ENABLED, DISABLED;

        fun description(): String {
            return when (this) {
                ENABLED -> "GPS ACTIVADO"
                DISABLED -> "GPS DESACTIVADO"
            }
        }

        fun icon(): Int {
            return when (this) {
                ENABLED -> R.drawable.point_green
                DISABLED -> R.drawable.point_red
            }
        }

        fun animation(): Int? {
            return when (this) {
                ENABLED -> null
                DISABLED -> R.raw.disconnected
            }
        }
    }
