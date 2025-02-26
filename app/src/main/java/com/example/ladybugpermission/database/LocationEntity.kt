package com.example.ladybugpermission.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    val locationId: Int,
    val latitude: Double,
    val longitude: Double,
    val updateAt: Long,
)
