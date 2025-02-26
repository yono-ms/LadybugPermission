package com.example.ladybugpermission.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(locationEntity: LocationEntity)

    @Delete
    suspend fun deleteLocation(locationEntity: LocationEntity)

    @Query("SELECT * from locations ORDER BY updateAt DESC")
    fun getAllLocationsFlow(): Flow<List<LocationEntity>>
}
