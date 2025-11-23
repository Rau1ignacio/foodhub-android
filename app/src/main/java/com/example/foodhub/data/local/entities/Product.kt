package com.example.foodhub.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val price: Int,
    val description: String = "",
    val stock: Int = 0,
    val category: String = "Otros",
    val available: Boolean = true,
    val imageUrl: String = ""
)