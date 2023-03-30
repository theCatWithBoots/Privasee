package com.example.privasee.database.database.viewmodel.repository

import androidx.lifecycle.LiveData
import com.example.privasee.database.database.viewmodel.repository.dao.UserDao
import com.example.privasee.database.model.User

class UserRepository(private val userDao: UserDao) {

    val getAllDataLive: LiveData<List<User>> = userDao.getAllDataLive()

    fun getOwnerId(isOwner: Boolean): Int {
        return userDao.getOwnerId(isOwner)
    }

    fun getAllData(): List<User> {
        return userDao.getAllData()
    }

    fun getAllUserId(): List<Int> {
        return userDao.getAllUserId()
    }

    fun getUserId(name: String): Int {
        return userDao.getUserId(name)
    }

    suspend fun addUser(user: User) {
        userDao.addUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    fun getLastInsertedUser(): User {
        return userDao.getLastInsertedUser()
    }
}