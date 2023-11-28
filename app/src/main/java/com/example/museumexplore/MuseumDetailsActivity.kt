package com.example.museumexplore

import android.os.Bundle
import android.widget.ImageView
import com.example.museumexplore.databinding.MuseumDetailsBinding
import com.example.museumexplore.modules.ImageAdapter
import com.example.museumexplore.modules.ImagesModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy


class MuseumDetailsActivity : TopBarActivity() {

    private lateinit var binding: MuseumDetailsBinding
    private val list = ArrayList<ImagesModel>()
    private val adapter = ImageAdapter(list, this)

    val snapHelper = CarouselSnapHelper()
    val layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MuseumDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)





        binding.apply {

            carouselRecyclerView.setLayoutManager(layoutManager)
            carouselRecyclerView.adapter = adapter

            list.add(ImagesModel(R.drawable.group_30, "Image title"))
            list.add(ImagesModel(R.drawable.group_30, "Image title1"))
            list.add(ImagesModel(R.drawable.group_30, "Image title2"))
            list.add(ImagesModel(R.drawable.group_30, "Image title3"))
            list.add(ImagesModel(R.drawable.group_30, "Image title4"))
            list.add(ImagesModel(R.drawable.group_30, "Image title5"))

        }

        val backArrow = findViewById<ImageView>(R.id.BackArrowIcon)
        val drawer = findViewById<ImageView>(R.id.drawerIcon)
        setupBackButton(backArrow)
        setupDrawerButton(drawer)

        val museumName = intent.extras?.getString(EXTRA_NAME)

        binding.textViewMuseumName.text = museumName

    }

    companion object{
        const val EXTRA_NAME = "extra_name"
    }
}