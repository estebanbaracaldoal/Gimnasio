package com.esteban.gimnasio.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.esteban.gimnasio.data.entities.Workout


@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(workout: Workout)
    @Update
    suspend fun update(workout: Workout)
    @Delete
    suspend fun delete(workout: Workout)
    @Query("SELECT * FROM workouts ORDER BY workoutName ASC")
    fun getAllWorkouts(): LiveData<List<Workout>>
    @Query("SELECT * FROM workouts WHERE level LIKE :searchLevel ORDER BY workoutName ASC")
    fun getWorkoutsByLevel(searchLevel: String): LiveData<List<Workout>>
    @Query("SELECT * FROM workouts WHERE id = :workoutId")
    suspend fun getWorkoutById(workoutId: Int): Workout?



}