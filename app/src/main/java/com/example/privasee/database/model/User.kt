package com.example.privasee.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val isOwner: Boolean
) : Parcelable
