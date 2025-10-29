package com.esteban.gimnasio.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "t_users",)
data class EntiUser(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "password")
    val password: String,

   @ColumnInfo(name = "firtsName")
    val firstName: String,

    @ColumnInfo(name = "lastName")
    val lastName: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "dateBirth")
    val dateBirth: String,

    @ColumnInfo(name = "rememberMe")
    val rememberMe: String,
)