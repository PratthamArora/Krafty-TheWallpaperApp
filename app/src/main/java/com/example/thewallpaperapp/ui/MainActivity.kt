package com.example.thewallpaperapp.ui

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.thewallpaperapp.ui.HomeActivity
import com.krafty.android.R
import org.jetbrains.anko.startActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Handler().postDelayed({
            startActivity<HomeActivity>()
            finish()
        }, 3000)
    }
}

