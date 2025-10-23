package com.example.foodhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodhub.data.local.entities.User

// DAO para manejar operaciones relacionadas con usuarios
@Dao
interface UserDao {

    // Inserta un nuevo usuario, ignorando el registro si ya existe uno con el mismo correo
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    // Busca un usuario por su correo electrónico y devuelve el primero que coincida
    @Query("SELECT * FROM User WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): User?
}
