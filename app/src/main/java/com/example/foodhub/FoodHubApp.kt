package com.example.foodhub

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class  FoodHubApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ensureNotificationChannel()
    }

    private fun ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "orders_channel", // ID interna: La usarás para decidir por dónde enviar la notificación.
                "Pedidos",     // Nombre visible: El usuario lo verá en los Ajustes de la app.
                NotificationManager.IMPORTANCE_DEFAULT // Prioridad: Define si suena, vibra, etc.
            ).apply {
                // Descripción: Texto que el usuario ve en los Ajustes
                description = "Notificaciones sobre confirmación de pedidos"
            }
            // Registrar el canal en el sistema
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}