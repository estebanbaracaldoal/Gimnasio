package com.esteban.gimnasio.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val workoutName: String,
    val level: String,
    val numExercises: Int,
    val videoUrl: String
)