package com.pabs.operadores_funeraria.data.persistence

import android.content.Context
import android.content.SharedPreferences

enum class PreferencesKey(val value: String){
    USER("user"),
    AUTH_TOKEN("auth_token"),
    SERVICIO("servicio")
}

object PreferencesProvider {

    fun set(context: Context, key: PreferencesKey, value: String? = null){
        val editor = prefs(context).edit()
        editor.putString(key.value, value).apply()
    }

    fun remove(context: Context, key: PreferencesKey){
        val editor = prefs(context).edit()
        editor.remove(key.value).apply()
    }

    fun string(context: Context, key: PreferencesKey): String?{
        return prefs(context).getString(key.value, null)
    }

    fun clear(context: Context){
        val editor = prefs(context).edit()
        editor.clear().apply()
    }

    private fun prefs(context: Context): SharedPreferences {
        return  context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }
}