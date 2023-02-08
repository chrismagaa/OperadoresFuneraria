package com.pabs.operadores_funeraria.data.network.model

import android.os.Parcelable
import com.google.gson.GsonBuilder

class User(
    val id: Int,
    val username: String,
    val role: String,
    val autoPlaca: String,
    val urlServiceSocket: String
) {
    companion object{
        fun toJson(user: User): String{
            return GsonBuilder().create().toJson(user)
        }

        fun fromJson(json: String): User{
            return GsonBuilder().create().fromJson(json, User::class.java)
        }
    }
}