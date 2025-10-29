package com.esteban.gimnasio.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "users",)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "login")
    val login: String,

    @ColumnInfo(name = "password_hash")
    val passwordHash: String,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "apellidos")
    val apellidos: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "fecha_nacimiento")
    val fechaNacimientoTimestamp: Long,

    @ColumnInfo(name = "tipo_usuario") // "Cliente" o "Entrenador"
    val tipoUsuario: String
)