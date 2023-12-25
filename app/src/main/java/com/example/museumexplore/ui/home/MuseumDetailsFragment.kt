    package com.example.museumexplore.ui.home

    import android.graphics.BitmapFactory
    import android.os.Bundle
    import android.util.Log
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import androidx.appcompat.app.AppCompatActivity
    import androidx.fragment.app.Fragment
    import androidx.navigation.NavController
    import androidx.navigation.Navigation
    import com.example.museumexplore.R
    import com.example.museumexplore.databinding.FragmentMuseumDetailsBinding
    import com.example.museumexplore.modules.EventAdpater
    import com.example.museumexplore.modules.Event
    import com.example.museumexplore.modules.Image
    import com.example.museumexplore.modules.ImageAdapter
    import com.google.android.material.carousel.CarouselLayoutManager
    import com.google.android.material.carousel.CarouselSnapHelper
    import com.google.android.material.carousel.HeroCarouselStrategy
    import com.google.firebase.firestore.ktx.firestore
    import com.google.firebase.ktx.Firebase
    import com.google.firebase.storage.ktx.storage

    class MuseumDetailsFragment : Fragment() {

        private var _binding: FragmentMuseumDetailsBinding? = null
        private val binding get() = _binding!!

        private lateinit var navController: NavController

        private val museumImagesList = arrayListOf<Image>()
        private lateinit var museumImagesAdapter: ImageAdapter
        private val artWorksList = arrayListOf<Image>()
        private lateinit var artWorksAdapter: ImageAdapter
        private val eventList = ArrayList<Event>()
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
            return binding.root
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
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
                val bundle = Bundle()
                bundle.putString("museumId", id)
                bundle.putString("museumName", name)
                navController.navigate(R.id.action_museumDetailsFragment_to_artWorksFragment, bundle)
            }

            val db = Firebase.firestore
            db.collection("museums/$id/imagesCollectionMuseum")
                .addSnapshotListener { snapshot, error ->
                    snapshot?.documents?.let {
                        this.museumImagesList.clear()
                        for (document in it) {
                            document.data?.let{ data ->
                                this.museumImagesList.add(
                                    Image.fromSnapshot(
                                        document.id,
                                        data
                                    )
                                )
                            }
                        }
                        this.museumImagesAdapter.notifyDataSetChanged()
                    }
                }

            db.collection("museums/$id/imagesCollectionArtWork")
                .addSnapshotListener { snapshot, error ->
                    snapshot?.documents?.let {
                        this.artWorksList.clear()
                        for (document in it) {
                            document.data?.let{ data ->
                                this.artWorksList.add(
                                    Image.fromSnapshot(
                                        document.id,
                                        data
                                    )
                                )
                            }
                        }
                        this.artWorksAdapter.notifyDataSetChanged()
                    }
                }

            db.collection("museums/$id/events")
                .addSnapshotListener { snapshot, error ->
                    snapshot?.documents?.let {
                        this.eventList.clear()
                        for (document in it) {
                            document.data?.let{ data ->
                                this.eventList.add(
                                    Event.fromSnapshot(
                                        document.id,
                                        data
                                    )
                                )
                            }
                        }
                        this.eventsAdapter.notifyDataSetChanged()
                    }
                }

            binding.apply {

                carouselRecyclerViewMuseumImages.layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
                museumImagesAdapter = ImageAdapter(museumImagesList, requireContext())
                snapHelper.attachToRecyclerView(carouselRecyclerViewMuseumImages)
                carouselRecyclerViewMuseumImages.adapter = museumImagesAdapter

                carouselRecyclerViewArtWorksImages.layoutManager = CarouselLayoutManager(HeroCarouselStrategy())
                artWorksAdapter = ImageAdapter(artWorksList, requireContext())
                snapHelper.attachToRecyclerView(carouselRecyclerViewArtWorksImages)
                carouselRecyclerViewArtWorksImages.adapter = artWorksAdapter

                eventsAdapter = EventAdpater(eventList, requireContext()) {
                    val bundle = Bundle()
                    bundle.putString("eventId", it.id)
                    bundle.putString("eventTitle", it.title)
                    bundle.putString("eventDescription", it.description)
                    bundle.putString("eventPathToImage", it.pathToImage)
                    navController.navigate(R.id.action_museumDetailsFragment_to_eventDetailsFragment, bundle)
                }
                snapHelper.attachToRecyclerView(carouselRecyclerViewEvents)
                carouselRecyclerViewEvents.adapter = eventsAdapter

                //viewPagerEvents.adapter = adapter
            }


        }
    }