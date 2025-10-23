package com.example.foodhub.domain.models

import com.example.foodhub.core.utils.FieldError
import com.example.foodhub.core.utils.Validators
import com.example.foodhub.data.local.entities.Product

// Modelo de dominio que representa el formulario de producto usado en la interfaz
data class ProductForm(
    val id: Long = 0, // ID del producto, 0 si es nuevo
    val name: String = "", // Nombre del producto
    val price: String = "", // Precio ingresado como texto
    val stock: String = "", // Stock ingresado como texto
    val category: String = "", // Categoría del producto
    val available: Boolean = true, // Indica si el producto está disponible
    val photoUri: String? = null, // Imagen del producto (opcional)
) {

    // Valida los campos del formulario y devuelve un mapa de errores por campo
    fun validate(): Map<String, FieldError> = buildMap {
        Validators.name(name)?.let { put("name", it) }
        Validators.price(price)?.let { put("price", it) }
        Validators.stock(stock)?.let { put("stock", it) }
    }

    // Indica si el formulario es válido (sin errores)
    val isValid: Boolean get() = validate().isEmpty()

    // Convierte el formulario validado en una entidad Product lista para guardar en base de datos
    fun toEntity(): Product = Product(
        id = id,
        name = name.trim(),
        price = price.trim().toInt(),
        stock = stock.trim().toInt(),
        category = category.trim(),
        available = available,
        photoUri = photoUri
    )
}
