package com.example.foodhub.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.foodhub.data.local.dao.CartDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.foodhub.data.local.dao.OrderDao
import com.example.foodhub.data.local.dao.ProductDao
import com.example.foodhub.data.local.dao.UserDao
import com.example.foodhub.data.local.entities.CartItem
import com.example.foodhub.data.local.entities.Order
import com.example.foodhub.data.local.entities.User
import com.example.foodhub.data.local.entities.Product

@Database(
    entities = [Product::class, CartItem::class, Order::class, User::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun build(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "foodhub.db"
                )
                    .fallbackToDestructiveMigration() // Borra la BD si hay conflicto de versiones
                    .addCallback(AppDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Callback para datos iniciales LOCALES (Solo para caché)
    private class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    // Nota: Estos usuarios solo existirán en tu celular, NO en el Backend.
                    // Como ahora usas login en la nube, estos usuarios locales no servirán para loguearse
                    // a menos que los crees también en Spring Boot.
                    val userDao = database.userDao()

                    userDao.insert(
                        User(
                            name = "Admin Local",
                            email = "admin@local.com",
                            password = "123",
                            role = "ADMIN"
                        )
                    )
                }
            }
        }
    }
}