package com.pabs.operadores_funeraria.core

import android.util.Log
import com.pabs.operadores_funeraria.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

class Interceptor(): Interceptor {

    val TAG = "Interceptor"
    override fun intercept(chain: Interceptor.Chain): Response {
        return if(!isInternetAvailable()){
            throw NoInternetException()
        }else{
            val original = chain.request()
            val request = original.newBuilder()
                .header("Authorization", "dsad")

                .build()

            if(BuildConfig.DEBUG) {
                Log.d(TAG, request.toString())
            }

            chain.proceed(request)
        }
    }

    private fun isInternetAvailable(): Boolean {
        return try {
            val timeoutMs = 1500
            val sock = Socket()
            val sockaddr = InetSocketAddress("8.8.8.8", 53)
            sock.connect(sockaddr, timeoutMs)
            sock.close()

            true
        } catch (e: IOException) {
            false
        }
    }

    class NoInternetException() : IOException() {
        override val message: String
            get() =
                "Internet no disponible, verifica tu conexión a internet"
    }

}