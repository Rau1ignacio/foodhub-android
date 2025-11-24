package com.example.foodhub.data.local.dao

import androidx.room.*
import com.example.foodhub.data.local.entities.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun observeProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products ORDER BY id DESC")
    suspend fun getAll(): List<Product>

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

    @Query("DELETE FROM products")
    suspend fun clearAll()

    // Reducir stock localmente sin que baje de 0
    @Query("UPDATE products SET stock = MAX(stock - :quantity, 0) WHERE id = :productId")
    suspend fun decreaseStock(productId: Long, quantity: Int)
}
