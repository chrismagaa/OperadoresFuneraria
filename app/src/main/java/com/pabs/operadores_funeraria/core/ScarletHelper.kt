package com.pabs.operadores_funeraria.core


import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object ScarletHelper {

    fun provideScarlet(
        socketUrl: String,
        client: OkHttpClient,
        streamAdapterFactory: StreamAdapter.Factory,
    ) =
        Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory(socketUrl))
            .addStreamAdapterFactory(streamAdapterFactory)
            .build()


    fun provideOkhttp() =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build()

}