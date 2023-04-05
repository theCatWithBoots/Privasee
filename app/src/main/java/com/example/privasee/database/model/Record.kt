package com.example.privasee.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity (tableName = "record")

data class Record(
    @PrimaryKey (autoGenerate = true) val id : Int = 0,
    val day : Int,
    val month: Int,
    val year: Int,
    val time : Long,
    val packageName : String
)
