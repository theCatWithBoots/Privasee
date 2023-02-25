package com.example.privasee.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.privasee.database.model.User

@Dao
interface PrivaSeeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addUser(user: User)

    @Query("SELECT * FROM user ORDER BY id ASC")
    fun readAllData(): LiveData<List<User>>

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

}