package com.example.gr34_in2000_v21.data.repository

import com.example.gr34_in2000_v21.data.local.PersistentDatabase
import com.example.gr34_in2000_v21.data.remote.GeoNorgeDataSource
import javax.inject.Inject

class GeoNorgeRepository @Inject constructor(
    private val remote: GeoNorgeDataSource,
    private val database: PersistentDatabase
) : BaseRepository() {
    fun getNetworkPunkt(lat: Double, lon: Double) = makeRequest { remote.getPunkt(lat, lon) }
}