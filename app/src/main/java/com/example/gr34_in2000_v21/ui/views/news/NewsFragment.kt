package com.example.gr34_in2000_v21.ui.views.news

import android.annotation.SuppressLint
import android.graphics.Color
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.gr34_in2000_v21.databinding.NewsFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import android.content.SharedPreferences

@AndroidEntryPoint
class NewsFragment : Fragment() {
    private var _binding: NewsFragmentBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = NewsFragmentBinding.inflate(inflater, container, false)
        val myWebView: WebView = binding.webview

        myWebView.settings.javaScriptEnabled = true
        myWebView.webViewClient = WebViewClient()
        myWebView.setBackgroundColor(Color.TRANSPARENT)

        val sharedPreferences: SharedPreferences? = activity?.getSharedPreferences(
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
            myWebView.loadUrl("file:///android_asset/twitter_timeline_darkmode.html")
        } else {
            myWebView.loadUrl("file:///android_asset/twitter_timeline.html")
        }

        // Disables links for testing purposes
        /*
        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return true
            }
        }
         */

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}