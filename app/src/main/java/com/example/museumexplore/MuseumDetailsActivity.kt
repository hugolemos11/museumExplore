package com.example.museumexplore


import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import com.example.museumexplore.databinding.MuseumDetailsBinding
import com.example.museumexplore.modules.EventAdpater
import com.example.museumexplore.modules.EventsModel
import com.example.museumexplore.modules.ImageAdapter
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy

class MuseumDetailsActivity : TopBarActivity() {

    private lateinit var binding: MuseumDetailsBinding
    private val museumImagesList = ArrayList<Int>()
    private val museumImagesAdapter = ImageAdapter(museumImagesList, this)
    private val artWorksList = ArrayList<Int>()
    private val artWorksAdapter = ImageAdapter(artWorksList, this)
    private val eventList = ArrayList<EventsModel>()
    //private val adapter = EventsPagerAdapter(eventList, this)
    private val eventsAdapter = EventAdpater(eventList, this)

    val snapHelper = CarouselSnapHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MuseumDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            //carouselRecyclerViewMuseumImages.layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
            snapHelper.attachToRecyclerView(carouselRecyclerViewMuseumImages)
            carouselRecyclerViewMuseumImages.adapter = museumImagesAdapter

            museumImagesList.add(R.drawable.rectangle_368)
            museumImagesList.add(R.drawable.rectangle_369)
            museumImagesList.add(R.drawable.rectangle_370)
            museumImagesList.add(R.drawable.rectangle_371)
            museumImagesList.add(R.drawable.rectangle_372)

            //carouselRecyclerViewArtWorksImages.layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
            snapHelper.attachToRecyclerView(carouselRecyclerViewArtWorksImages)
            carouselRecyclerViewArtWorksImages.adapter = artWorksAdapter

            artWorksList.add(R.drawable.rectangle_368)
            artWorksList.add(R.drawable.rectangle_369)
            artWorksList.add(R.drawable.rectangle_370)
            artWorksList.add(R.drawable.rectangle_371)
            artWorksList.add(R.drawable.rectangle_372)

            snapHelper.attachToRecyclerView(carouselRecyclerViewEvents)
            carouselRecyclerViewEvents.adapter = eventsAdapter

            eventList.add(EventsModel(R.drawable.rectangle_375, "Event 1", "Description for Event 1"))
            eventList.add(EventsModel(R.drawable.rectangle_376, "Event 2", "Description for Event 2"))

            //viewPagerEvents.adapter = adapter
        }




        val backArrow = findViewById<ImageView>(R.id.BackArrowIcon)
        val drawer = findViewById<ImageView>(R.id.drawerIcon)
        setupBackButton(backArrow)
        setupDrawerButton(drawer)

        val museumName = intent.extras?.getString(EXTRA_NAME)
        val museumDescription = intent.extras?.getString(EXTRA_DESCRIPTION)

        binding.textViewMuseumName.text = museumName
        binding.textViewDescription.text = museumDescription

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("Not yet implemented")
    }

    companion object{
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_DESCRIPTION = "extra_description"
    }
}