package com.example.foodhubtest.core.utils

// Representa un error de validación en un campo
data class FieldError(val message: String)

// Contiene funciones de validación para distintos tipos de campos
object Validators {

    // Valida que el nombre no esté vacío
    fun name(v: String): FieldError? {
        val s = v.trim()
        return if (s.isEmpty()) FieldError("El nombre es obligatorio") else null
    }

    // Valida que el precio sea un número entero mayor a 0
    fun price(v: String): FieldError? {
        val value = v.trim().toIntOrNull() ?: return FieldError("Precio inválido")
        return if (value <= 0) FieldError("El precio debe ser mayor a 0") else null
    }

    // Valida que el stock sea un número entero no negativo
    fun stock(v: String): FieldError? {
        val value = v.trim().toIntOrNull() ?: return FieldError("Stock inválido")
        return if (value < 0) FieldError("Stock no puede ser negativo") else null
    }

    // Valida que el correo tenga un formato correcto
    fun email(v: String): FieldError? {
        val s = v.trim()
        return when {
            s.isEmpty() -> FieldError("El correo es obligatorio")
            !s.contains("@") || !s.contains(".") -> FieldError("El formato del correo es inválido")
            else -> null
        }
    }

    // Valida que la contraseña tenga al menos 6 caracteres
    fun password(v: String): FieldError? {
        return if (v.length < 6) FieldError("La contraseña debe tener al menos 6 caracteres") else null
    }
}
