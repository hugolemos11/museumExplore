    package com.example.museumexplore.ui.home

    import android.graphics.BitmapFactory
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.MenuItem
    import android.view.View
    import android.view.ViewGroup
    import androidx.appcompat.app.AppCompatActivity
    import androidx.fragment.app.Fragment
    import androidx.navigation.NavController
    import androidx.navigation.Navigation
    import com.example.museumexplore.R
    import com.example.museumexplore.databinding.FragmentMuseumDetailsBinding
    import com.example.museumexplore.modules.EventAdpater
    import com.example.museumexplore.modules.EventsModel
    import com.example.museumexplore.modules.ImageAdapter
    import com.google.android.material.carousel.CarouselLayoutManager
    import com.google.android.material.carousel.CarouselSnapHelper
    import com.google.android.material.carousel.HeroCarouselStrategy
    import com.google.firebase.ktx.Firebase
    import com.google.firebase.storage.ktx.storage

    class MuseumDetailsFragment : Fragment() {

        private var _binding: FragmentMuseumDetailsBinding? = null
        private val binding get() = _binding!!

        private lateinit var navController: NavController

        private val museumImagesList = ArrayList<Int>()
        private lateinit var museumImagesAdapter: ImageAdapter
        private val artWorksList = ArrayList<Int>()
        private lateinit var artWorksAdapter: ImageAdapter
        private val eventList = ArrayList<EventsModel>()
        private lateinit var eventsAdapter: EventAdpater
        //private val adapter = EventsPagerAdapter(eventList, this)
        private var id : String? = null
        private var name : String? = null
        private var description : String? = null
        private var location : String? = null
        private var rate : Int? = null
        private var pathToImage : String? = null

        val snapHelper = CarouselSnapHelper()

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentMuseumDetailsBinding.inflate(inflater, container, false)
            name = arguments?.getString(EXTRA_NAME)
            description = arguments?.getString(EXTRA_DESCRIPTION)
            return binding.root
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            arguments?.let { bundle ->
                id = bundle.getString("museumId")
                name = bundle.getString("museumName")
                description = bundle.getString("museumDescription")
                location = bundle.getString("museumLocation")
                rate = bundle.getInt("museumRate")
                pathToImage = bundle.getString("museumPathToImage")
            }

            // Remove the title of fragment on the actionBar
            (activity as AppCompatActivity).supportActionBar?.title = ""

            navController = Navigation.findNavController(view);

            pathToImage?.let { imagePath ->
                // Load the image from Firebase Storage
                val storage = Firebase.storage
                val storageRef = storage.reference
                val pathReference = storageRef.child(imagePath)
                val ONE_MEGABYTE: Long = 10 * 1024 * 1024
                pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { data ->
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.count())
                    binding.museumImage.setImageBitmap(bitmap)
                }.addOnFailureListener {
                    // Handle any errors
                    Log.e("MuseumDetailsFragment", "Failed to load image from Firebase Storage")
                }
            }
            binding.textViewMuseumName.text = name
            binding.textViewDescription.text = description

            binding.buttonCollection.setOnClickListener {
                navController.navigate(R.id.action_museumDetailsFragment_to_artWorksFragment)
            }

            binding.apply {

                carouselRecyclerViewMuseumImages.layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
                museumImagesAdapter = ImageAdapter(museumImagesList, requireContext())
                snapHelper.attachToRecyclerView(carouselRecyclerViewMuseumImages)
                carouselRecyclerViewMuseumImages.adapter = museumImagesAdapter

                museumImagesList.add(R.drawable.museu_interior1)
                museumImagesList.add(R.drawable.museu_interior2)
                museumImagesList.add(R.drawable.museu_interior3)
                museumImagesList.add(R.drawable.museu_interior4)

                carouselRecyclerViewArtWorksImages.layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
                artWorksAdapter = ImageAdapter(artWorksList, requireContext())
                snapHelper.attachToRecyclerView(carouselRecyclerViewArtWorksImages)
                carouselRecyclerViewArtWorksImages.adapter = artWorksAdapter

                artWorksList.add(R.drawable.museu_interior1)
                artWorksList.add(R.drawable.museu_interior2)
                artWorksList.add(R.drawable.museu_interior3)
                artWorksList.add(R.drawable.museu_interior4)

                eventsAdapter = EventAdpater(eventList, requireContext())
                snapHelper.attachToRecyclerView(carouselRecyclerViewEvents)
                carouselRecyclerViewEvents.adapter = eventsAdapter

                eventList.add(EventsModel(R.drawable.rectangle_375, "Event 1", "Description for Event 1"))
                eventList.add(EventsModel(R.drawable.rectangle_376, "Event 2", "Description for Event 2"))

                //viewPagerEvents.adapter = adapter
            }
        }

        companion object{
            const val EXTRA_NAME = "extra_name"
            const val EXTRA_DESCRIPTION = "extra_description"
        }
    }