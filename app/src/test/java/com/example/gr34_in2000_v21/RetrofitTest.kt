package com.example.gr34_in2000_v21

import com.example.gr34_in2000_v21.data.remote.GeoNorgeService
import com.example.gr34_in2000_v21.data.remote.MetAlertsService
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitTest {
    // Replica of module in app
    // (building retrofit in a local unit test creates an illegal reflective access operation error. Tests continue to work as intended.)
    private fun remoteMET(): MetAlertsService {
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

    private fun remoteGEO(): GeoNorgeService {
        val logging = HttpLoggingInterceptor()
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(logging)
        return Retrofit.Builder()
            .baseUrl("https://ws.geonorge.no/kommuneinfo/v1/")
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GeoNorgeService::class.java)
    }


    @Test
    fun createMetServiceAndCall() {
            val s = remoteMET()
            val i = runBlocking {
                val i = s.get()
                i
            }
            Assert.assertEquals(200, i.code())
    }

    @Test
    fun createGeoServiceAndCall() {
        val s = remoteGEO()
        val i = runBlocking {
            val i = s.getPunkt(50.90000, 10.70000)
            i
        }
        Assert.assertEquals(200, i.code())
    }
}