package com.example.museumexplore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast

open class TopBarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.top_bar)
    }

    fun setupBackButton(backArrowIcon: ImageView) {
        backArrowIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setupDrawerButton(drawerIcon: ImageView) {
        drawerIcon.setOnClickListener {
            // Open drawer
            //Toast.makeText(this, "Test", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}