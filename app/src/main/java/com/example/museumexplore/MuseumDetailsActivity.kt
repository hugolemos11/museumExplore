package com.example.museumexplore

import android.os.Bundle
import android.widget.ImageView

import com.example.museumexplore.databinding.MuseumDetailsBinding

class MuseumDetailsActivity : TopBarActivity() {

    private lateinit var binding: MuseumDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MuseumDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backArrow = findViewById<ImageView>(R.id.BackArrowIcon)
        val drawer = findViewById<ImageView>(R.id.drawerIcon)
        setupBackButton(backArrow)
        setupDrawerButton(drawer)
    }
}