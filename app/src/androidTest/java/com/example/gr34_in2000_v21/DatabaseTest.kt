package com.example.gr34_in2000_v21

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gr34_in2000_v21.data.local.CacheDatabase
import com.example.gr34_in2000_v21.data.local.GeoNorgeDao
import com.example.gr34_in2000_v21.data.local.MetAlertsDao
import com.example.gr34_in2000_v21.data.local.PersistentDatabase
import com.example.gr34_in2000_v21.data.models.GeoNorgeModel
import com.example.gr34_in2000_v21.utils.Helpers.Test.getOrAwaitValue
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // mock counties
    private val mocks = listOf(
        GeoNorgeModel.CountyCoordinates(null, "Oslo", null, listOf(GeoNorgeModel.GyldigeNavn("Oslo", null, null)), "Oslo",
            "Oslo", null, GeoNorgeModel.PunktIOmrade(listOf(0.0,0.0), null), samiskForvaltningsomrade = false,isFavorite = false),
        GeoNorgeModel.CountyCoordinates(null, "Viken", null, listOf(GeoNorgeModel.GyldigeNavn("Frogn", null, null)), "Oslo",
            "Frogn", null, GeoNorgeModel.PunktIOmrade(listOf(0.0,0.0), null), samiskForvaltningsomrade = false,isFavorite = false),
        GeoNorgeModel.CountyCoordinates(null, "Nordland", null, listOf(GeoNorgeModel.GyldigeNavn("Bodø", null, null)), "Oslo",
            "Bodø", null, GeoNorgeModel.PunktIOmrade(listOf(0.0,0.0), null),  samiskForvaltningsomrade = false,isFavorite = false)
    )

    private lateinit var geoNorgeDao: GeoNorgeDao
    private lateinit var cacheDatabase: CacheDatabase
    private lateinit var metAlertsDao: MetAlertsDao
    private lateinit var persistentDatabase: PersistentDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        cacheDatabase = Room.inMemoryDatabaseBuilder(
            context, CacheDatabase::class.java).build()
        metAlertsDao = cacheDatabase.metalertsDao()
        persistentDatabase = Room.inMemoryDatabaseBuilder(
            context, PersistentDatabase::class.java).build()
        geoNorgeDao = persistentDatabase.geonorgeDao()

        GlobalScope.launch {
            geoNorgeDao.insertAll(mocks)
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        cacheDatabase.close()
        persistentDatabase.close()
    }


    @Test
    @Throws(Exception::class)
    fun removeFavoriteCounty() = runBlocking {
        val county = removeFromFavorites("Oslo")

        if (county == null) assert(false) else geoNorgeDao.update(county)

        if (geoNorgeDao.allFavorites.getOrAwaitValue().any { it == county })
            assert(false)
        else
            assert(true)
    }

    @Test
    @Throws(Exception::class)
    fun addFavoriteCounty() = runBlocking {
        val county = addToFavorites("Oslo")

        if (county == null) assert(false) else geoNorgeDao.update(county)

        if (geoNorgeDao.allFavorites.getOrAwaitValue().any { it == county })
            assert(true)
        else
            assert(false)
    }


    /*
        Function that adds a CountyCoordinates object as a favorite (replica)
     */
    fun addToFavorites(countyName: String): GeoNorgeModel.CountyCoordinates? {
        for (county in mocks) {
            if (county.kommunenavn == countyName) {
                county.isFavorite = true
                return county
            }
        }
        return null
    }

    /*
        Function that removes a county object as a favorite (replica)
     */
    fun removeFromFavorites(countyName: String): GeoNorgeModel.CountyCoordinates? {
        for (county in mocks) {
            if (county.kommunenavn == countyName) {
                county.isFavorite = false
                return county
            }
        }
        return null
    }

}