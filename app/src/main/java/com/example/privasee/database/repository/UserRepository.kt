package com.example.privasee.database.repository

import androidx.lifecycle.LiveData
import com.example.privasee.database.PrivaSeeDao
import com.example.privasee.database.model.User

class UserRepository (private val privaSeeDao: PrivaSeeDao) {

    val readAllData: LiveData<List<User>> = privaSeeDao.readAllData()

    suspend fun addUser(user: User) {
        privaSeeDao.addUser(user)
    }

    suspend fun updateUser(user: User) {
        privaSeeDao.updateUser(user)
    }

    suspend fun deleteUser(user: User) {
        privaSeeDao.deleteUser(user)
    }
}