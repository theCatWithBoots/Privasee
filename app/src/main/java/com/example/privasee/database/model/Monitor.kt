package com.example.privasee.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitor")
data class Monitor(
    @PrimaryKey (autoGenerate = true) val id : Int,
    val app_name : String
)
