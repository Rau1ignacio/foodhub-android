package com.example.foodhub.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad que representa un producto disponible en la aplicación
@Entity
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID autogenerado del producto
    val name: String, // Nombre del producto
    val price: Int, // Precio del producto
    val stock: Int, // Cantidad disponible en inventario
    val category: String, // Categoría a la que pertenece el producto
    val available: Boolean, // Indica si el producto está disponible para la venta
    val photoUri: String? = null // URI opcional de la imagen del producto
)
