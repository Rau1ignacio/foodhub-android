package com.example.foodhub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.foodhub.data.local.entities.CartItem
import com.example.foodhub.data.local.entities.Order
import com.example.foodhub.data.local.entities.Product
import kotlinx.coroutines.flow.Flow

// DAO para acceder y manipular los datos de productos y del carrito
@Dao
interface ProductDao {

    // Obtiene todos los productos ordenados por ID descendente
    @Query("SELECT * FROM Product ORDER BY id DESC")
    fun observeProducts(): Flow<List<Product>>

    // Inserta un nuevo producto y devuelve el ID generado
    @Insert
    suspend fun insert(product: Product): Long

    // Actualiza un producto existente
    @Update
    suspend fun update(product: Product)

    // Elimina un producto específico
    @Delete
    suspend fun delete(product: Product)

    // Obtiene un producto por su ID
    @Query("SELECT * FROM Product WHERE id = :id")
    suspend fun getById(id: Long): Product?

    // Obtiene todos los ítems del carrito en tiempo real
    @Query("SELECT * FROM CartItem")
    fun observeCartItems(): Flow<List<CartItem>>

    // Inserta o reemplaza un ítem en el carrito
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCartItem(item: CartItem)

    // Elimina un ítem del carrito
    @Delete
    suspend fun deleteCartItem(item: CartItem)

    // Vacía completamente el carrito
    @Query("DELETE FROM CartItem")
    suspend fun clearCart()

    // Inserta una nueva orden y devuelve su ID generado
    @Insert
    suspend fun insertOrder(order: Order): Long

    // Actualizar stock
    @Query("UPDATE Product SET stock = :newStock WHERE id = :productId")
    suspend fun updateStock(productId: Long, newStock: Int)
}
