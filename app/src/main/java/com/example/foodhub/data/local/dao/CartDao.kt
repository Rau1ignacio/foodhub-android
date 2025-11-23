package com.example.foodhub.data.local.dao

import androidx.room.*
import com.example.foodhub.data.local.entities.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // Devuelve un Flow para que la UI se actualice automáticamente cuando cambia el carrito
    @Query("SELECT * FROM cart_items")
    fun observeCartItems(): Flow<List<CartItem>>

    // Busca un item específico por el ID del producto (útil para saber si ya lo agregaste)
    @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
    suspend fun getCartItemByProductId(productId: Long): CartItem?

    // Inserta o Actualiza: Si el item ya tiene ID, lo actualiza. Si no, lo crea.
    // Nota: Requiere Room 2.5.0 o superior. Si usas una versión vieja, usa @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Upsert
    suspend fun upsertCartItem(item: CartItem)

    // Borra un item específico (cuando le das al tachito de basura)
    @Delete
    suspend fun deleteCartItem(item: CartItem)

    // Vacía el carrito completo (cuando se confirma la compra)
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}