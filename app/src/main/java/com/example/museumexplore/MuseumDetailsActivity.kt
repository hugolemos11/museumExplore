package com.example.museumexplore

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import com.example.museumexplore.databinding.MuseumDetailsBinding
import com.example.museumexplore.modules.EventAdpater
import com.example.museumexplore.modules.ImageAdapter
import com.example.museumexplore.modules.EventsModel
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.android.material.carousel.HeroCarouselStrategy


class MuseumDetailsActivity : TopBarActivity() {

    private lateinit var binding: MuseumDetailsBinding
    private val museumImagesList = ArrayList<Int>()
    private val museumImagesAdapter = ImageAdapter(museumImagesList, this)
    private val artWorksList = ArrayList<Int>()
    private val artWorksAdapter = ImageAdapter(artWorksList, this)
    private val eventsList = ArrayList<EventsModel>()
    private val eventsAdapter = EventAdpater(eventsList, this)

    //val snapHelper = CarouselSnapHelper()
    val layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MuseumDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

            //carouselRecyclerViewMuseumImages.layoutManager = layoutManager
            carouselRecyclerViewMuseumImages.adapter = museumImagesAdapter

            museumImagesList.add(R.drawable.group_30)
            museumImagesList.add(R.drawable.group_30)
            museumImagesList.add(R.drawable.group_30)
            museumImagesList.add(R.drawable.group_30)
            museumImagesList.add(R.drawable.group_30)
            museumImagesList.add(R.drawable.group_30)

            //carouselRecyclerViewArtWorksImages.layoutManager = layoutManager
            carouselRecyclerViewArtWorksImages.adapter = artWorksAdapter

            artWorksList.add(R.drawable.group_30)
            artWorksList.add(R.drawable.group_30)
            artWorksList.add(R.drawable.group_30)
            artWorksList.add(R.drawable.group_30)
            artWorksList.add(R.drawable.group_30)
            artWorksList.add(R.drawable.group_30)

            carouselRecyclerViewEvents.layoutManager = layoutManager
            carouselRecyclerViewEvents.adapter = eventsAdapter

            eventsList.add(EventsModel(R.drawable.group_30, "Teste", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
            eventsList.add(EventsModel(R.drawable.group_30, "Teste", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
            eventsList.add(EventsModel(R.drawable.group_30, "Teste", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
            eventsList.add(EventsModel(R.drawable.group_30, "Teste", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
            eventsList.add(EventsModel(R.drawable.group_30, "Teste", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
            eventsList.add(EventsModel(R.drawable.group_30, "Teste", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
            eventsList.add(EventsModel(R.drawable.group_30, "Teste", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))

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