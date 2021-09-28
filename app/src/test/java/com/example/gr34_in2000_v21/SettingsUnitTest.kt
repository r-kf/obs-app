package com.example.gr34_in2000_v21

import android.content.Context
import androidx.core.content.edit
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.example.gr34_in2000_v21.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Assert.assertEquals
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
class SettingsUnitTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)
    private lateinit var activity: ActivityScenario<MainActivity>

    @Before
    fun setup() {
        // SharedViewModel prevents Robolectric from using the activity directly on a local unit test.
        // activity = ActivityScenario.launch(MainActivity::class.java)
        hiltRule.inject()
    }

    /*
    @Test
    fun setDarkMode() {
        var isDarkModeOn = false
        activity.onActivity {
            it.findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_settings)
            it.findViewById<Switch>(R.id.nattmodus).performClick()
            isDarkModeOn = it.sharedPreferences.getBoolean("isDarkModeOn", false)
        }
        shadowOf(getMainLooper()).idle()
        assertEquals(true, isDarkModeOn)
    }*/


    @Test
    fun testDarkModeSetting() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPreferences = context.getSharedPreferences(
            "sharedPrefs", Context.MODE_PRIVATE
        )
        assertEquals(false, sharedPreferences.getBoolean("isDarkModeOn", false))
        sharedPreferences.edit {
            this.putBoolean("isDarkModeOn", true)
        }
        assertEquals(true, sharedPreferences.getBoolean("isDarkModeOn", false))
    }
}