package com.esteban.gimnasio.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.esteban.gimnasio.data.dao.GimnasioDao
import com.esteban.gimnasio.data.MyRoomDatabase
import com.esteban.gimnasio.data.dao.WorkoutDao
import com.esteban.gimnasio.data.entities.GimnasioEntity
import com.esteban.gimnasio.data.entities.Workout
import kotlin.synchronized

@Database(
    entities = [Workout::class, GimnasioEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MyRoomDatabase : RoomDatabase() {

    abstract fun workoutDao(): WorkoutDao
    abstract fun gimnasioDao(): GimnasioDao


    companion object {
        @Volatile
        private var INSTANCE: MyRoomDatabase? = null

        fun getDatabase(context: Context): MyRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyRoomDatabase::class.java,
                    "gimnasio_app_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}