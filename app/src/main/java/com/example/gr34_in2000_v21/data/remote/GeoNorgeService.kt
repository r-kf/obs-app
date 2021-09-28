package com.example.gr34_in2000_v21.data.remote

import com.example.gr34_in2000_v21.data.models.GeoNorgeModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// https://ws.geonorge.no/kommuneinfo/v1/#/default/get_punkt
interface GeoNorgeService {
    @GET("punkt?koordsys=4258")
    suspend fun getPunkt(
        @Query("nord") lat: Double,
        @Query("ost") lon: Double
    ): Response<GeoNorgeModel.Coordinate>
}