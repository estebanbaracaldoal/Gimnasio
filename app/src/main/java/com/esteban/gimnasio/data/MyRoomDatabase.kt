package com.esteban.gimnasio.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.esteban.gimnasio.data.dao.UserDao
import com.esteban.gimnasio.data.entities.EntiUser
import kotlin.synchronized


@Database (entities = [EntiUser::class], version = 1)

abstract class MyRoomDatabase :  RoomDatabase() {

    companion object {
        @Volatile
        private var instance: MyRoomDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also()
            {instance = it}
        }
        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            MyRoomDatabase::class.java,
            "gimnasio.db").build()
    }
    abstract fun userDao(): UserDao
}