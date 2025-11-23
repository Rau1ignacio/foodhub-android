package com.example.foodhub.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val productId: Long,
    val name: String,
    val price: Int,
    val quantity: Int,
    val imageUrl: String?
)