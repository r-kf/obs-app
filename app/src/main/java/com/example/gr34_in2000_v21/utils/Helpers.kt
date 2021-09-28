package com.example.gr34_in2000_v21.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.TypeConverter
import com.example.gr34_in2000_v21.data.models.GeoNorgeModel
import com.example.gr34_in2000_v21.data.models.MetAlertsModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * This is a collection of objects meant for assistance when developing
 * Feel free to use them
 */
object Helpers {

    object StringFix {
        fun getGuidFromLink(guid: String) =
            guid.replace("https://in2000-apiproxy.ifi.uio.no/weatherapi/metalerts/1.1?cap=", "")

        fun norwegianizeSeverity(original: String) = when (original) {
            "Moderate" -> "Moderat"
            else -> original
        }
    }

    object MetAlerts {
        fun rss2List(rss: MetAlertsModel.RSS): List<MetAlertsModel.Item>? = rss.channel?.items
        class CAPInfoTypeConverter {
            @TypeConverter
            fun storedStringToCAPInfo(value: String): List<MetAlertsModel.CAPInfo>? {
                if (value.isBlank())
                    return emptyList()
                val listType = object : TypeToken<List<MetAlertsModel.CAPInfo>>() {}.type
                return Gson().fromJson<List<MetAlertsModel.CAPInfo>>(value, listType)
            }

            @TypeConverter
            fun storedCAPInfoToString(caps: List<MetAlertsModel.CAPInfo>?): String? {
                return Gson().toJson(caps)
            }
        }
    }

    object GeoNorge {
        class CountyCoordinatesTypeConverter {
            @TypeConverter
            fun storedStringToGyldigeNavn(value: String): List<GeoNorgeModel.GyldigeNavn?> {
                if (value.isBlank())
                    return emptyList()
                val listType = object : TypeToken<List<GeoNorgeModel.GyldigeNavn>>() {}.type
                return Gson().fromJson(value, listType)
            }

            @TypeConverter
            fun storedCAPInfoToString(caps: List<GeoNorgeModel.GyldigeNavn?>): String {
                return Gson().toJson(caps)
            }

            @TypeConverter
            fun storedStringToAvgrensningsboks(value: String): GeoNorgeModel.Avgrensningsboks? {
                val listType = object : TypeToken<GeoNorgeModel.Avgrensningsboks>() {}.type
                return Gson().fromJson(value, listType)
            }

            @TypeConverter
            fun storedAvgrensningsboksToString(caps: GeoNorgeModel.Avgrensningsboks?): String? {
                return Gson().toJson(caps)
            }

            @TypeConverter
            fun storedStringToPunktIOmrade(value: String): GeoNorgeModel.PunktIOmrade? {
                if (value.isBlank())
                    return null
                val listType = object : TypeToken<GeoNorgeModel.PunktIOmrade>() {}.type
                return Gson().fromJson(value, listType)
            }

            @TypeConverter
            fun storedPunktIOmradeToString(caps: GeoNorgeModel.PunktIOmrade?): String? {
                return Gson().toJson(caps)
            }
        }
    }

    object Location {
        fun checkPermissions(ctx: Context): Boolean = ActivityCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        fun requestPermissions(act: Activity) = ActivityCompat.requestPermissions(
            act, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 44
        )

        fun isLocationEnabled(ctx: Context): Boolean {
            val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }
    }

    object Test {
        fun <T> LiveData<T>.getOrAwaitValue(
            time: Long = 2,
            timeUnit: TimeUnit = TimeUnit.SECONDS
        ): T {
            var data: T? = null
            val latch = CountDownLatch(1)
            val observer = object : Observer<T> {
                override fun onChanged(o: T?) {
                    data = o
                    latch.countDown()
                    this@getOrAwaitValue.removeObserver(this)
                }
            }

            this.observeForever(observer)

            // Don't wait indefinitely if the LiveData is not set.
            if (!latch.await(time, timeUnit)) {
                throw TimeoutException("LiveData value was never set.")
            }

            @Suppress("UNCHECKED_CAST")
            return data as T
        }
    }
}