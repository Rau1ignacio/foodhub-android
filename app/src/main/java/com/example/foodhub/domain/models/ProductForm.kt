package com.example.foodhub.domain.models

import com.example.foodhub.core.utils.FieldError
import com.example.foodhub.core.utils.Validators
import com.example.foodhub.data.local.entities.Product

data class ProductForm(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val stock: String = "",
    val category: String = "",
    val imageUrl: String = ""
) {
    fun validate(): Map<String, FieldError> = buildMap {
        Validators.name(name)?.let { put("name", it) }
        Validators.price(price)?.let { put("price", it) }
        Validators.stock(stock)?.let { put("stock", it) }
    }

    val isValid: Boolean get() = validate().isEmpty()

    fun toEntity(): Product = Product(
        id = id,
        name = name.trim(),
        description = description.trim(),
        price = price.trim().toInt(),
        imageUrl = imageUrl.trim(),
        category = category.ifBlank { "Otros" },
        stock = stock.trim().toInt()
    )
}
