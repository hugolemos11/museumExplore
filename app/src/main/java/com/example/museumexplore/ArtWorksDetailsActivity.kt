/*package com.example.museumexplore

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.museumexplore.databinding.ArtWorksDetailsBinding

class ArtWorksDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ArtWorksDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ArtWorksDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*val backArrow = findViewById<ImageView>(R.id.BackArrowIcon)
        val drawer = findViewById<ImageView>(R.id.drawerIcon)
        setupBackButton(backArrow)
        setupDrawerButton(drawer)*/

        val artWorksName = intent.extras?.getString(EXTRA_NAME)
        val artistName = intent.extras?.getString(EXTRA_ARTIST)
        val year = intent.extras?.getInt(EXTRA_YEAR, 0)
        val category = intent.extras?.getString(EXTRA_CATEGORY)
        val description = intent.extras?.getString(EXTRA_DESCRIPTION)

        // arranjar uma forma melhor
        val drawableId = intent.extras?.getInt(EXTRA_IMAGE)?: 0
        val drawable = ContextCompat.getDrawable(this, drawableId)

        binding.imageViewArtWorkImage.setImageDrawable(drawable)
        binding.textViewArtWorkName.text = artWorksName
        binding.textViewArtistNameYear.text = artistName//"${artistName} ${year}"
        binding.textViewArtWorkCategory.text =  category
        binding.textViewArtWorkDescription.text = description
    }

    companion object{
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_YEAR = "extra_year"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_DESCRIPTION = "extra_description"
    }
}*/