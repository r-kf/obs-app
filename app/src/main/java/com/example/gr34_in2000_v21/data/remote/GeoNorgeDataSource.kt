package com.example.gr34_in2000_v21.data.remote

import javax.inject.Inject

class GeoNorgeDataSource @Inject constructor(
    private val service: GeoNorgeService
) : BaseDataSource() {
    suspend fun getPunkt(lat: Double, lon: Double) = getResult { service.getPunkt(lat, lon) }
}