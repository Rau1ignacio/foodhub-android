package com.example.foodhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.foodhub.data.local.entities.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Query("DELETE FROM users")
    suspend fun clearUsers()

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): User?
}
