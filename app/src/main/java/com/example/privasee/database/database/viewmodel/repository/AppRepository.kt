package com.example.privasee.database.database.viewmodel.repository

import androidx.lifecycle.LiveData
import com.example.privasee.database.model.App
import com.example.privasee.database.database.viewmodel.repository.dao.AppDao

class AppRepository(private val appDao: AppDao) {

    fun getAllDataLive(): LiveData<List<App>> {
        return appDao.getAllDataLive()
    }

    suspend fun addApp(app: App) {
        appDao.addApp(app)
    }

    fun getAllData(): List<App> {
        return appDao.getAllData()
    }

    fun getAllAppId(): List<Int> {
        return appDao.getAllAppId()
    }

    fun getAppName(packageId: Int): String {
        return appDao.getAppName(packageId)
    }
}