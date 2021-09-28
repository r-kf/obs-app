package com.example.gr34_in2000_v21

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.test.core.app.ApplicationProvider
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
class NewsUnitTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun webviewLoadTest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val webview = WebView(context)
        webview.settings.javaScriptEnabled = true
        webview.webViewClient = WebViewClient()
        webview.setBackgroundColor(Color.TRANSPARENT)

        val sharedPreferences: SharedPreferences? = context.getSharedPreferences(
            "sharedPrefs", Context.MODE_PRIVATE
        )
        val isDarkModeOn = sharedPreferences
            ?.getBoolean(
                "isDarkModeOn", false
            )

        /*
        * Local webpage/html file in assets
        */
        if (isDarkModeOn == true) {
            webview.loadUrl("file:///android_asset/twitter_timeline_darkmode.html")
            assertEquals("file:///android_asset/twitter_timeline_darkmode.html", webview.url)
        } else {
            webview.loadUrl("file:///android_asset/twitter_timeline.html")
            assertEquals("file:///android_asset/twitter_timeline.html", webview.url)
        }
    }
}