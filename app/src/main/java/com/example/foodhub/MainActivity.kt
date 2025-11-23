package com.example.foodhub

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.core.app.ActivityCompat
import com.example.foodhub.core.nav.AppNav
import com.example.foodhub.data.local.AppDatabase
import com.example.foodhub.data.repository.FoodRepository

class MainActivity : ComponentActivity() {

    private val db by lazy { AppDatabase.build(this) }

    // CORRECCIÓN: Pasamos los 4 DAOs requeridos
    private val repo by lazy {
        FoodRepository(
            productDao = db.productDao(),
            cartDao = db.cartDao(),
            orderDao = db.orderDao(),
            userDao = db.userDao()
        )
    }

    private val requestPostNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        setContent {
            MaterialTheme {
                AppNav(repo = repo)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("orders_channel", "Pedidos", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}