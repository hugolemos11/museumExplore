package com.example.museumexplore

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

abstract class TopBarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.top_bar)
    }

    fun setupBackButton(backArrowIcon: ImageView) {
        backArrowIcon.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setupDrawerButton(drawerIcon: ImageView, ) {
        drawerIcon.setOnClickListener {
            // Open drawer
            //Toast.makeText(this, "Test", Toast.LENGTH_LONG).show()
            /*val drawerLayout: DrawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

            val toolbar = findViewById<Toolbar>(R.id.toolbar)

            val navigationView = findViewById<NavigationView>(R.id.nav_view)
            navigationView.setNavigationItemSelectedListener(this)

            val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)

            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()*/



            //val intent = Intent(this, LoginActivity::class.java)
            //startActivity(intent)
        }
    }
}