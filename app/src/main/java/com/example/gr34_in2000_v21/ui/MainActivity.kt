package com.example.gr34_in2000_v21.ui


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.example.gr34_in2000_v21.R
import com.example.gr34_in2000_v21.databinding.ActivityMainBinding
import com.example.gr34_in2000_v21.ui.intro.WelcomeActivity
import com.google.android.material.bottomappbar.BottomAppBar
import dagger.hilt.android.AndroidEntryPoint


/**
 * MainActivity of the entire app
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: SharedViewModel by viewModels()

    lateinit var endrer: SharedPreferences.Editor

    lateinit var sharedPreferences: SharedPreferences

    private lateinit var starterIntent: Intent

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("CommitPrefEdits") // Is used in settings fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Fab_Bottom_app_bar)
        super.onCreate(savedInstanceState)

        starterIntent = intent

        // Saving state of our app using SharedPreferences
        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        endrer = sharedPreferences.edit()
        val isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initUI()
        initVM()

        //Starts dark mode if the user turned it on
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        val imageView = findViewById<ImageView>(R.id.image)

        //Adds a background image
        when {
            sharedPreferences.getString("bakgrunn", "ingen") == "1" -> {
                Glide.with(this).load(R.drawable.bakgrunn1).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "2" -> {
                Glide.with(this).load(R.drawable.bakgrunn2).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "3" -> {
                Glide.with(this).load(R.drawable.bakgrunn3).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "4" -> {
                Glide.with(this).load(R.drawable.bakgrunn4).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "5" -> {
                Glide.with(this).load(R.drawable.bakgrunn5).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "6" -> {
                Glide.with(this).load(R.drawable.bakgrunn6).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "7" -> {
                Glide.with(this).load(R.drawable.bakgrunn7).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "8" -> {
                Glide.with(this).load(R.drawable.night1).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "9" -> {
                Glide.with(this).load(R.drawable.night2).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "10" -> {
                Glide.with(this).load(R.drawable.night3).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "11" -> {
                Glide.with(this).load(R.drawable.night4).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "12" -> {
                Glide.with(this).load(R.drawable.night5).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "13" -> {
                Glide.with(this).load(R.drawable.night6).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "14" -> {
                Glide.with(this).load(R.drawable.night7).into(imageView)
            }
            sharedPreferences.getString("bakgrunn", "ingen") == "0" -> {
                Glide.with(this).load(R.drawable.bakgrunn_ingen).into(imageView)
            }
        }
    }

    private fun initVM() {
        // Load data
        viewModel.getAllItems()
    }

    private fun initUI() {
        val sharedPreferences = getSharedPreferences(
            "sharedPrefs", Context.MODE_PRIVATE
        )
        val firstTime = sharedPreferences.getBoolean("FIRST_TIME", true)

        // Here should everything of UI code go in
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false //placeholder
        val bottom: BottomAppBar = findViewById(R.id.bottomAppBar)
        binding.bottomNavigationView.setBackgroundColor(Color.TRANSPARENT)
        bottom.setBackgroundColor(Color.parseColor("#C7ffffff"))

        setupUI()

        //Intro
        if (firstTime) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
    }

    private fun setupUI() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        NavigationUI.setupWithNavController(
            binding.bottomNavigationView,
            navHostFragment.navController
        )
        binding.fab.setOnClickListener {
            navHostFragment.navController.navigate(R.id.navigation_search)
        }
    }

}