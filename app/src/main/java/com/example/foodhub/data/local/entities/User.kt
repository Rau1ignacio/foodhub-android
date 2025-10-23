package com.example.foodhub.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad que representa un usuario registrado en la aplicación
@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID autogenerado del usuario
    val name: String, // Nombre del usuario
    val email: String, // Correo electrónico del usuario
    val passwordHash: String, // Contraseña almacenada como hash, no en texto plano
    val role: String = "CLIENT" // Rol del usuario, por defecto es cliente
)
