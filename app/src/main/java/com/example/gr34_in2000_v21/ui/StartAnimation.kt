package com.example.gr34_in2000_v21.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.gr34_in2000_v21.R

/*
    Resources:
    https://www.youtube.com/watch?v=F82x3gUkF04
    https://www.developer.android.com/about/versions/12/features/splash-screen
 */

class StartAnimation : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_start_animation)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }



        supportActionBar?.hide()

        val videoView: VideoView = findViewById(R.id.startAnimation)

        val video: Uri =
            Uri.parse("android.resource://" + packageName + "/" + R.raw.start_animation)

        videoView.setVideoURI(video)

        videoView.setOnCompletionListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        videoView.start()
    }
}