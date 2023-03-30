package com.example.privasee.database.database.viewmodel.repository.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.privasee.database.model.Restriction

@Dao
interface RestrictionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addRestriction(restriction: Restriction)

    // App Monitoring Access queries
    @Query("SELECT * FROM restriction " +
            "WHERE monitored = true " +
            "AND userId = :userId")
    fun getAllMonitoredApps(userId: Int): LiveData<List<Restriction>>

    @Query("SELECT * FROM restriction " +
            "WHERE monitored = false " +
            "AND userId = :userId")
    fun getAllUnmonitoredApps(userId: Int): LiveData<List<Restriction>>

    @Query("UPDATE restriction SET monitored = :isMonitored " +
            "WHERE id = :restrictionId")
    fun updateMonitoredApps(restrictionId: Int, isMonitored: Boolean)

    // App Controlling Access queries
    @Query("SELECT * FROM restriction " +
            "WHERE controlled = true " +
            "AND userId = :userId")
    fun getAllControlledApps(userId: Int): LiveData<List<Restriction>>

    @Query("SELECT * FROM restriction " +
            "WHERE controlled = false " +
            "AND userId = :userId")
    fun getAllUncontrolledApps(userId: Int): LiveData<List<Restriction>>

    @Query("UPDATE restriction SET controlled = :isControlled " +
            "WHERE id = :restrictionId")
    fun updateControlledApps(restrictionId: Int, isControlled: Boolean)

}