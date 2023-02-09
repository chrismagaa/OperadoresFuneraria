package com.pabs.operadores_funeraria.core


import com.pabs.operadores_funeraria.utils.Constantes
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.MessageAdapter
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.retry.BackoffStrategy
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory

object ScarletHelper {

    fun provideScarlet(
        socketUrl: String,
        client: OkHttpClient,
        lifecycle: Lifecycle,
        streamAdapterFactory: StreamAdapter.Factory,
        backoffStrategy: BackoffStrategy
    ) =
        Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory(socketUrl))
            .lifecycle(lifecycle)
            .addStreamAdapterFactory(streamAdapterFactory)
            .backoffStrategy(backoffStrategy)
            .build()


    fun provideOkhttp() =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build()

}