package com.example.privasee.database.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.privasee.database.database.PrivaSeeDatabase
import com.example.privasee.database.model.Restriction
import com.example.privasee.database.database.viewmodel.repository.RestrictionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RestrictionViewModel(application: Application): AndroidViewModel(application) {

    private val repository: RestrictionRepository

    init {
        val restrictionDao = PrivaSeeDatabase.getDatabase(application).restrictionDao()
        repository = RestrictionRepository(restrictionDao)
    }

    fun addRestriction(restriction: Restriction) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRestriction(restriction)
        }
    }

    // Monitoring Access viewmodel
    fun getAllMonitoredApps(userId: Int): LiveData<List<Restriction>> {
        return repository.getAllMonitoredApps(userId)
    }

    fun getAllUnmonitoredApps(userId: Int): LiveData<List<Restriction>> {
        return repository.getAllUnmonitoredApps(userId)
    }

    fun updateMonitoredApps(restrictionId: Int, isMonitored: Boolean) {
        return repository.updateMonitoredApps(restrictionId, isMonitored)
    }

    // Controlling Access viewmodel
    fun getAllControlledApps(userId: Int): LiveData<List<Restriction>> {
        return repository.getAllControlledApps(userId)
    }

    fun getAllUncontrolledApps(userId: Int): LiveData<List<Restriction>> {
        return repository.getAllUncontrolledApps(userId)
    }

    fun updateControlledApps(restrictionId: Int, isControlled: Boolean) {
        return repository.updateControlledApps(restrictionId, isControlled)
    }
}