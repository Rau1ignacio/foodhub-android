package com.example.foodhub.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val total: Int,
    val itemsSummary: String,
    val timestamp: Long,
    val status: String = "PENDING"
)