package com.example.foodhub.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String = "",
    val description: String = "",
    val price: Int = 0,
    val imageUrl: String = "",
    val category: String = "Otros",
    // Stock disponible
    val stock: Int = 0
) {
    val isAvailable: Boolean
        get() = stock > 0
}
