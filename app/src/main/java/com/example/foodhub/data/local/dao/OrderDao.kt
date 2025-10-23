package com.example.foodhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.foodhub.data.local.entities.Order
import kotlinx.coroutines.flow.Flow

// DAO para acceder a los datos de las órdenes en la base de datos
@Dao
interface OrderDao {

    // Inserta una nueva orden y devuelve el ID generado
    @Insert
    suspend fun insert(order: Order): Long

    // Obtiene todas las órdenes de un usuario específico ordenadas por fecha descendente
    @Query("SELECT * FROM `Order` WHERE userId = :userId ORDER BY timestamp DESC")
    fun getOrdersForUser(userId: Long): Flow<List<Order>>
}
