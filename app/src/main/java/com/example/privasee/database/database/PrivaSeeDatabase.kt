package com.example.privasee.database.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.privasee.database.model.App
import com.example.privasee.database.model.Monitor
import com.example.privasee.database.model.Restriction
import com.example.privasee.database.model.User
import com.example.privasee.database.database.viewmodel.repository.dao.AppDao
import com.example.privasee.database.database.viewmodel.repository.dao.RestrictionDao
import com.example.privasee.database.database.viewmodel.repository.dao.UserDao

@Database (entities = [User::class, Monitor::class, App::class, Restriction::class],
            version = 2,
            exportSchema = false)

abstract class PrivaSeeDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun appDao(): AppDao
    abstract fun restrictionDao(): RestrictionDao

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