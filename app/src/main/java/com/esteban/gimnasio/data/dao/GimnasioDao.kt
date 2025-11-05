package com.esteban.gimnasio.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.esteban.gimnasio.data.entities.GimnasioEntity

@Dao
interface GimnasioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: GimnasioEntity): Long

    @Query("SELECT * FROM t_users WHERE username = :username AND password = :password")
    suspend fun getUserByCredentials(username: String, password: String): GimnasioEntity?

    @Query("SELECT * FROM t_users WHERE username = :username")
    suspend fun getUserByUsername(username: String): GimnasioEntity?

    @Query("SELECT * FROM t_users WHERE email = :email")
    suspend fun getUserByEmail(email: String): GimnasioEntity?

    @Query("SELECT * FROM t_users WHERE id = :id")
    suspend fun getUserById(id: Long): GimnasioEntity?

    @Query("SELECT * FROM t_users")
    suspend fun getAllUsers(): List<GimnasioEntity>

    @Update
    suspend fun updateUser(user: GimnasioEntity)

    @Delete
    suspend fun deleteUser(user: GimnasioEntity)

    @Query("DELETE FROM t_users WHERE id = :id")
    suspend fun deleteUserById(id: Long)

    @Query("SELECT COUNT(*) FROM t_users")
    suspend fun countUsers(): Long
}