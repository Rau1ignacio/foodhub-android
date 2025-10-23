package com.example.foodhubtest.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Entidad que representa una orden de compra realizada por un usuario
@Entity(
    indices = [Index(value = ["userId"])],
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )
)
data class Order(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // ID autogenerado de la orden
    val userId: Long, // ID del usuario que realizó la orden
    val timestamp: Long = System.currentTimeMillis(), // Fecha y hora de creación de la orden
    val total: Int // Total del monto de la orden
)
