package com.example.foodhub.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.foodhub.data.local.entities.Product
import com.example.foodhub.data.local.entities.CartItem

@Dao
interface ProductDao {

    // --- PRODUCTOS (Caché Local) ---
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun observeProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Long): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    // --- CARRITO (Local) ---
    @Query("SELECT * FROM cart_items")
    fun observeCartItems(): Flow<List<CartItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCartItem(item: CartItem)

    @Delete
    suspend fun deleteCartItem(item: CartItem)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

}