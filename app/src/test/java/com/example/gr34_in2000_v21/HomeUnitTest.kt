package com.example.gr34_in2000_v21

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

/*
    https://developer.android.com/training/testing/unit-testing/local-unit-tests
    https://junit.org/junit4/javadoc/latest/org/junit/Assert.html
*/
@HiltAndroidTest
@Config(sdk = [29], application = HiltTestApplication::class)
@RunWith(
    RobolectricTestRunner::class)
@LooperMode(LooperMode.Mode.PAUSED)
class HomeUnitTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun testLocation() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val locationServices = LocationServices.getFusedLocationProviderClient(
            context
        )
        locationServices.lastLocation.addOnSuccessListener {
            assert(true)
        }.exception?.let {
            assert(false)
        }
    }

}