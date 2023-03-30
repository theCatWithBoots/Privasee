package com.example.privasee.database.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.privasee.database.database.PrivaSeeDatabase
import com.example.privasee.database.model.User
import com.example.privasee.database.database.viewmodel.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val userDao = PrivaSeeDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    val getAllDataLive: LiveData<List<User>> = repository.getAllDataLive

    fun getAllData(): List<User> {
        return repository.getAllData()
    }

    fun getAllUserId(): List<Int> {
        return repository.getAllUserId()
    }

    fun getUserId(name: String): Int {
        return repository.getUserId(name)
    }

    fun getOwnerId(isOwner: Boolean): Int {
        return repository.getOwnerId(isOwner)
    }

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUser(user)
        }
    }

    fun getLastInsertedUser(): User {
        return repository.getLastInsertedUser()
    }

}