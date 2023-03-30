package com.example.privasee.database.database.viewmodel.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.privasee.database.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM user ORDER BY id ASC")
    fun getAllDataLive(): LiveData<List<User>>

    @Query("SELECT * FROM user ORDER BY id ASC")
    fun getAllData(): List<User>

    @Query("SELECT id FROM user")
    fun getAllUserId(): List<Int>

    @Query("SELECT id FROM user WHERE name = :name")
    fun getUserId(name: String): Int

    // This is used in the system so that the owner entity will never be deleted.
    @Query("SELECT id FROM user WHERE isOwner = :isOwner")
    fun getOwnerId(isOwner: Boolean): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM user ORDER BY id DESC LIMIT 1")
    fun getLastInsertedUser(): User

}