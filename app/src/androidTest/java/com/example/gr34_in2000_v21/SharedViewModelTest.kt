package com.example.gr34_in2000_v21

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.gr34_in2000_v21.data.local.CacheDatabase
import com.example.gr34_in2000_v21.data.local.PersistentDatabase
import com.example.gr34_in2000_v21.data.models.DataResult
import com.example.gr34_in2000_v21.data.remote.GeoNorgeDataSource
import com.example.gr34_in2000_v21.data.remote.GeoNorgeService
import com.example.gr34_in2000_v21.data.remote.MetAlertsDataSource
import com.example.gr34_in2000_v21.data.remote.MetAlertsService
import com.example.gr34_in2000_v21.data.repository.GeoNorgeRepository
import com.example.gr34_in2000_v21.data.repository.MetAlertsRepository
import com.example.gr34_in2000_v21.ui.SharedViewModel
import com.example.gr34_in2000_v21.utils.Helpers.Test.getOrAwaitValue
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(AndroidJUnit4::class)
class
SharedViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var metAlertsRepository: MetAlertsRepository
    private lateinit var locationProviderClient: FusedLocationProviderClient
    private lateinit var geoNorgeRepository: GeoNorgeRepository
    private lateinit var persistentDatabase: PersistentDatabase
    private lateinit var app: Application

    @Before
    fun createEverything() {
        fun provideMetAlertsRetrofit(): MetAlertsService {
            val logging = HttpLoggingInterceptor()
            val okHttpClient = OkHttpClient.Builder()
            okHttpClient.addInterceptor(logging)
            val tikXml =
                TikXml.Builder().exceptionOnUnreadXml(false).build() // No need to parse unneeded data
            return Retrofit.Builder()
                .baseUrl("https://in2000-apiproxy.ifi.uio.no/weatherapi/metalerts/")
                .client(okHttpClient.build())
                .addConverterFactory(TikXmlConverterFactory.create(tikXml))
                .build().create(MetAlertsService::class.java)
        }
        fun provideGeoNorgeRetrofit(): GeoNorgeService {
            val logging = HttpLoggingInterceptor()
            val okHttpClient = OkHttpClient.Builder()
            okHttpClient.addInterceptor(logging)
            return Retrofit.Builder()
                .baseUrl("https://ws.geonorge.no/kommuneinfo/v1/")
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(GeoNorgeService::class.java)
        }


        val context = ApplicationProvider.getApplicationContext<Context>()
        val cacheDatabase = Room.inMemoryDatabaseBuilder(
            context, CacheDatabase::class.java).build()
        persistentDatabase = Room.inMemoryDatabaseBuilder(
            context, PersistentDatabase::class.java).build()
        metAlertsRepository = MetAlertsRepository(MetAlertsDataSource(provideMetAlertsRetrofit()), cacheDatabase)
        locationProviderClient = LocationServices.getFusedLocationProviderClient(
            context
        )
        geoNorgeRepository = GeoNorgeRepository(GeoNorgeDataSource(provideGeoNorgeRetrofit()), persistentDatabase)
        app = Application()
    }

    // test that we receive all items
    @Test
    @Throws(Exception::class)
    fun getAllItems() {
        val vm = SharedViewModel(metAlertsRepository, locationProviderClient, geoNorgeRepository, persistentDatabase, app)
        vm.getAllItems()
        Assert.assertEquals(null, vm.lastCode.getOrAwaitValue()) // if null, successful.
    }

    // test coordinates for finding items (for use with gps)
    @Test
    @Throws(Exception::class)
    fun getItemsFromLatLon() {
        val vm = SharedViewModel(metAlertsRepository, locationProviderClient, geoNorgeRepository, persistentDatabase, app)

        vm.getItemsFromLatLonWithCap(59.9, 10.7).observeForever {
            when (it.status) {
                DataResult.Status.SUCCESS -> assert(true) // on success, we got items from coordinates
                DataResult.Status.ERROR -> assert(false) // on error, we did not get items from coordinates
                DataResult.Status.LOADING -> {
                }
            }
        }
    }
}