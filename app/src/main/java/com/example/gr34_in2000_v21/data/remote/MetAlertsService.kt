package com.example.gr34_in2000_v21.data.remote

import com.example.gr34_in2000_v21.data.models.MetAlertsModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Opprett nye API-servicer på denne måten. https://square.github.io/retrofit/
 */

// https://api.met.no/weatherapi/metalerts/1.1/documentation#!/data/get

interface MetAlertsService {
    @GET("1.1/")
    suspend fun get(@Query("show") show: String? = null): Response<MetAlertsModel.RSS>

    @GET("1.1/")
    suspend fun county(
        @Query("county") county: String,
        @Query("show") show: String? = null
    ): Response<MetAlertsModel.RSS>

    @GET("1.1/")
    suspend fun getLatLon(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("show") show: String? = null
    ): Response<MetAlertsModel.RSS>

    @GET("1.1")
    suspend fun cap(@Query("cap") guid: String): Response<MetAlertsModel.CAPAlert>
}