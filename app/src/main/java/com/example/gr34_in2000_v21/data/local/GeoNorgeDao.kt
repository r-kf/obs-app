package com.example.gr34_in2000_v21.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.gr34_in2000_v21.data.models.GeoNorgeModel

@Dao
interface GeoNorgeDao {
    @Query("SELECT * from county")
    suspend fun getAll(): List<GeoNorgeModel.CountyCoordinates>

    @get:Query("SELECT * from county")
    val all: LiveData<List<GeoNorgeModel.CountyCoordinates>>

    @get:Query("SELECT * from county WHERE isFavorite = 1")
    val allFavorites: LiveData<List<GeoNorgeModel.CountyCoordinates>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<GeoNorgeModel.CountyCoordinates>)

    @Update
    suspend fun update(county: GeoNorgeModel.CountyCoordinates)
}