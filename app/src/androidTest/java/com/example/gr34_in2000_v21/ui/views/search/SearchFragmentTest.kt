package com.example.gr34_in2000_v21.ui.views.search

import com.example.gr34_in2000_v21.data.models.GeoNorgeModel
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchFragmentTest {
    @Test
    fun addFavoriteTest() {
        SearchFragment().addToFavorites("Oslo")
        assertEquals("Oslo", SearchFragment().favoritesList[0].fylkesnavn)
    }

    @Test
    fun removeFavoriteTest() {
        SearchFragment().initializeMalAlertItems()
        SearchFragment().addToFavorites("Oslo")
        SearchFragment().removeFromFavorites("Oslo")
        val emptyList = mutableListOf<GeoNorgeModel.CountyCoordinates>()
        assertEquals(emptyList, SearchFragment().favoritesList)
    }

}