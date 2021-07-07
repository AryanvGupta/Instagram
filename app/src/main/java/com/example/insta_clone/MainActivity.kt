package com.example.insta_clone

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.insta_clone.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var textView: TextView

    @SuppressLint("SetTextI18n")
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                textView.text = "Home"
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
                textView.text = "Search"
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                textView.text = "add_post"
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                textView.text = "notifications"
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                textView.text = "profile"
                return@OnNavigationItemSelectedListener true
            }
        }

        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        textView = findViewById(R.id.message)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

    }
}