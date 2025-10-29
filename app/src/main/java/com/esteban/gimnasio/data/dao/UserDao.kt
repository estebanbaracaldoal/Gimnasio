package com.esteban.gimnasio.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.esteban.gimnasio.data.entities.User

@Dao
interface UserDao {

    // INSERT
    @Insert
    suspend fun insertUser(user: User): Long

    // SELECT
    @Query("SELECT * FROM users WHERE login = :login AND password = :password")
    suspend fun getUser(login: String, password: String): User?

    @Query("SELECT * FROM users WHERE login = :login")
    suspend fun getUserByLogin(login: String): User?

    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    // UPDATE
    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET tema = :tema WHERE id = :userId")
    suspend fun updateUserTheme(userId: Long, tema: String)

    @Query("UPDATE users SET idioma = :idioma WHERE id = :userId")
    suspend fun updateUserLanguage(userId: Long, idioma: String)

    // DELETE
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Long)

    // DEBUG
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}
