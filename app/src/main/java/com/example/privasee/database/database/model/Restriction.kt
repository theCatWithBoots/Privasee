package com.example.privasee.database.model

import androidx.room.*

@Entity( tableName = "restriction",
        foreignKeys = [
            ForeignKey(
                entity = User::class,
                parentColumns = ["id"],
                childColumns = ["userId"],
                onDelete = ForeignKey.CASCADE
            ),
            ForeignKey(
                entity = App::class,
                parentColumns = ["id"],
                childColumns = ["packageId"],
                onDelete = ForeignKey.CASCADE
            )
        ],
        indices = [
            Index(value = ["userId"]),
            Index(value = ["packageId"])
        ]
)
data class Restriction(
    @PrimaryKey (autoGenerate = true) val id : Int = 0,
    val appName: String,
    val monitored: Boolean,
    val controlled: Boolean,
    val userId: Int,
    val packageId: Int
)