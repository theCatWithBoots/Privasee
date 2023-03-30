package com.example.privasee.database.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity (tableName = "monitor",
        foreignKeys = [
            ForeignKey(
                entity = User::class,
                parentColumns = ["id"],
                childColumns = ["userId"],
                onDelete = ForeignKey.NO_ACTION // Don't delete any records
            ),
            ForeignKey(
                entity = App::class,
                parentColumns = ["id"],
                childColumns = ["packageId"],
                onDelete = ForeignKey.NO_ACTION // Don't delete any records
            )
        ],
        indices = [
            Index(value = ["userId"]),
            Index(value = ["packageId"])
        ]
)

data class Monitor(
    @PrimaryKey (autoGenerate = true) val id : Int,
    val dateAccess : Long,
    val timeAccess : Long,
    val userId : Int,
    val packageId : Int
)
