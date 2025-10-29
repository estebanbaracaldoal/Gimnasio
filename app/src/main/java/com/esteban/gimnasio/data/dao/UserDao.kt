package com.esteban.gimnasio.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.esteban.gimnasio.data.entities.EntiUser

@Dao
interface UserDao {

    // INSERT
    @Insert
    suspend fun insertUser(user: EntiUser): Long

    // SELECT
    @Query("SELECT * FROM t_users WHERE username = :login AND password = :password")
    suspend fun getUser(login: String, password: String): EntiUser?

    @Query("SELECT * FROM t_users WHERE username = :login AND password = :password")
    suspend fun getUserByCredentials(login: String, password: String): EntiUser?

    @Query("SELECT * FROM t_users WHERE username = :login")
    suspend fun getUserByLogin(login: String): EntiUser?

    @Query("SELECT * FROM t_users WHERE email = :email")
    suspend fun getUserByEmail(email: String): EntiUser?

    @Query("SELECT * FROM t_users WHERE id = :id")
    suspend fun getUserById(id: Long): EntiUser?

    @Query("SELECT * FROM t_users")
    suspend fun getAllUsers(): List<EntiUser>

    // UPDATE
    @Update
    suspend fun updateUser(user: EntiUser)

    @Delete
    suspend fun deleteUser(user: EntiUser)

    @Query("DELETE FROM t_users WHERE id = :id")
    suspend fun deleteUser(id: Long)

/*
    @Query("UPDATE t_users SET tema = :tema WHERE id = :userId")
    suspend fun updateUserTheme(userId: Long, tema: String)

    @Query("UPDATE users SET idioma = :idioma WHERE id = :userId")
    suspend fun updateUserLanguage(userId: Long, idioma: String)

 */

}
