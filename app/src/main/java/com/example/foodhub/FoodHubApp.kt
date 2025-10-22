package com.example.foodhubtest

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class FoodHubApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ensureNotificationChannel()
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "orders_channel", // ID del canal
                "Pedidos",        // Nombre visible para el usuario
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones sobre confirmación de pedidos"
            }
            // Registrar el canal en el sistema
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}