package com.example.privasee.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.privasee.database.model.Monitor
import com.example.privasee.database.model.User

@Database (entities = [User::class, Monitor::class],
            version = 2,
            exportSchema = false)

abstract class PrivaSeeDatabase: RoomDatabase() {

    abstract fun privaSeeDao(): PrivaSeeDao

    companion object {
        @Volatile
        private var INSTANCE: PrivaSeeDatabase? = null

        fun getDatabase(context: Context): PrivaSeeDatabase {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PrivaSeeDatabase::class.java,
                    "PrivaSee_Database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }

    }
}