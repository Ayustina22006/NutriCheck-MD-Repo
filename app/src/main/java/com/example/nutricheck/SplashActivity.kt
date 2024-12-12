package com.example.nutricheck

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashGif: ImageView = findViewById(R.id.splashGif)

        Glide.with(this)
            .load(R.drawable.splash_nutripedia)
            .into(splashGif)

        Handler().postDelayed({
            // Pindah ke MainActivity setelah 3 detik
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()  // Tutup SplashActivity
        }, 4570)  // Durasi splash screen 3 detik
    }
}
