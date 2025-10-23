package com.example.foodhub.data.local.entities

import androidx.room.Entity

// Entidad que representa un ítem en el carrito de compras
@Entity(primaryKeys = ["productId"])
data class CartItem(
    val productId: Long,
    val quantity: Int
)
