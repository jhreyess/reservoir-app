package com.jhreyess.reservoir

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.jhreyess.reservoir.data.AppContainer
import com.jhreyess.reservoir.data.AppDataContainer
import com.jhreyess.reservoir.workers.PollDataWorker

class App : Application() {
    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)

        createNotificationChannel()

    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PollDataWorker.DAILY_UPDATE_CHANNEL_ID,
                "Mostrar datos diarios",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = "Muestra notificaciones de los niveles de agua y recomendaciones"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}