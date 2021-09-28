package com.example.gr34_in2000_v21.data.remote

import javax.inject.Inject

class MetAlertsDataSource @Inject constructor(
    private val service: MetAlertsService
) : BaseDataSource() {
    suspend fun getAllItems() = getResult { service.get() }
    suspend fun getCap(guid: String) = getResult { service.cap(guid) }

    /* fun getCountyItems(c: String, show: String? = null) =
        getResult { service.county(c, show) }*/

    suspend fun getLatLon(lat: Double, lon: Double, show: String? = null) =
        getResult { service.getLatLon(lat.toString(), lon.toString(), show) }
}