package com.example.gr34_in2000_v21.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
*/
object GeoNorgeModel {
    //Data class used for the API that finds Norway's counties
    data class County(
        val kommunenavn: String?,
        val kommunenavnNorsk: String?,
        val kommunenummer: String?
    )

    //Data class used for the API that finds which county a location belongs to according to its latitude and longitude
    data class Coordinate(
        val fylkesnavn: String?,
        val fylkesnummer: String?,
        val kommunenavn: String?,
        val kommunenummer: String?
    )

    //Data classes used for the API that finds the coordinates of a county
    @Entity(tableName = "county")
    data class CountyCoordinates(
        val avgrensningsboks: Avgrensningsboks?,
        val fylkesnavn: String?,
        val fylkesnummer: String?,
        val gyldigeNavn: List<GyldigeNavn>,
        @PrimaryKey val kommunenavn: String,
        val kommunenavnNorsk: String?,
        val kommunenummer: String?,
        val punktIOmrade: PunktIOmrade,
        val samiskForvaltningsomrade: Boolean?,
        var isFavorite: Boolean = false
    )

    data class Avgrensningsboks(
        val coordinates: List<List<List<Double>>>,
        val crs: Crs?,
        val type: String?
    )

    data class Crs(val properties: Properties, val type: String?)

    data class Properties(val name: String?)

    data class GyldigeNavn(val navn: String?, val prioritet: Int?, val sprak: String?)

    data class PunktIOmrade(val coordinates: List<Double>, val crs: Crs?)
}