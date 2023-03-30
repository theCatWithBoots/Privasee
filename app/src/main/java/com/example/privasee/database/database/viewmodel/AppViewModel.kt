package com.example.privasee.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.privasee.database.database.PrivaSeeDatabase
import com.example.privasee.database.model.App
import com.example.privasee.database.database.viewmodel.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository

    init {
        val appDao = PrivaSeeDatabase.getDatabase(application).appDao()
        repository = AppRepository(appDao)
    }

    fun getAllDataLive(): LiveData<List<App>> {
        return repository.getAllDataLive()
    }

    fun getAllData(): List<App> {
        return repository.getAllData()
    }

    fun readAllAppId(): List<Int> {
        return repository.getAllAppId()
    }

    fun getAppName(packageId: Int): String {
        return repository.getAppName(packageId)
    }

    fun addApp(app: App) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addApp(app)
        }
    }



}