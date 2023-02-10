package com.pabs.operadores_funeraria.utils

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.session.PlaybackState.ACTION_STOP
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import com.pabs.operadores_funeraria.R
import com.pabs.operadores_funeraria.ui.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                start()

            }
            ACTION_STOP -> {
                stop()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Seguiemiento de ubicación...")
            .setContentText("Localización: null")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(3000L)
            .catch { e ->
                e.printStackTrace()
            }.onEach { location ->
                val lat = location.latitude.toString()
                val long = location.longitude.toString()
                sendDataToMain(lat, long)

                val updateNotification = notification.setContentText(
                    "Localización: ($lat, $long)"
                )
                notificationManager.notify(1, updateNotification.build())

            }.launchIn(serviceScope)


        startForeground(1, notification.build())
    }

    private fun sendDataToMain(lat: String, long: String) {
        val intent = Intent("changeLocationReciver").putExtra(MainActivity.EXTRA_LATITUDE, lat).putExtra(
            MainActivity.EXTRA_LONGITUDE, long)
        sendBroadcast(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
