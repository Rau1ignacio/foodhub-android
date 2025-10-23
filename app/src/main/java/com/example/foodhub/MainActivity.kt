package com.example.foodhub

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.foodhub.core.nav.AppNav
import com.example.foodhub.data.local.AppDatabase
import com.example.foodhub.data.repository.FoodRepository
import com.example.foodhubtest.R

class MainActivity : ComponentActivity() {

    private val db by lazy { AppDatabase.build(this) }
    private val repo by lazy { FoodRepository(db.productDao(), db.userDao(), db.orderDao()) }

    private val requestPostNotifications =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermission()
        setContent {
            MaterialTheme {
                AppNav(repo = repo)
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPostNotifications.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun showOrderNotification(total: Int) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        val notif = NotificationCompat.Builder(this, "orders_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Pedido confirmado")
            .setContentText("Total $$total. ¡Gracias por comprar en FoodHub!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(1, notif)
    }
}