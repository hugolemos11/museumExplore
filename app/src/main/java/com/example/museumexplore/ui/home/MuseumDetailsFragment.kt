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
    import com.bumptech.glide.Glide
    import com.denzcoskun.imageslider.ImageSlider
    import com.denzcoskun.imageslider.constants.ScaleTypes
    import com.denzcoskun.imageslider.models.SlideModel
    import com.example.museumexplore.R
    import com.example.museumexplore.databinding.FragmentMuseumDetailsBinding
    import com.example.museumexplore.modules.EventAdapter
    import com.example.museumexplore.modules.Event
    import com.example.museumexplore.modules.Image
    import com.example.museumexplore.modules.ImageAdapter
    import com.example.museumexplore.modules.Museum
    import com.example.museumexplore.showToast
    import com.google.android.material.carousel.CarouselLayoutManager
    import com.google.android.material.carousel.CarouselSnapHelper
    import com.google.android.material.carousel.HeroCarouselStrategy
    import com.google.firebase.firestore.FirebaseFirestore
    import com.google.firebase.firestore.ktx.firestore
    import com.google.firebase.ktx.Firebase
    import com.google.firebase.storage.ktx.storage

    class MuseumDetailsFragment : Fragment() {

        private var _binding: FragmentMuseumDetailsBinding? = null
        private val binding get() = _binding!!

        private lateinit var navController: NavController

        private val museumImagesList = arrayListOf<Image>()
        private val artWorksList = arrayListOf<Image>()
        private val eventList = ArrayList<Event>()
        private var id : String? = null
        private var name : String? = null
        private var description : String? = null
        private var location : String? = null
        private var rate : Int? = null
        private var pathToImage : String? = null

        private val db = Firebase.firestore

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

            navController = Navigation.findNavController(view)

            pathToImage?.let { imagePath ->
                // Load the image from Firebase Storage
                val storage = Firebase.storage
                val storageRef = storage.reference
                val pathReference = storageRef.child(imagePath)
                pathReference.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(requireContext())
                        .load(uri)
                        .into(binding.museumImage)
                }.addOnFailureListener {
                    // Handle any errors
                }
            }


            binding.apply {
                textViewMuseumName.text = name
                textViewDescription.text = description

                buttonCollection.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("museumId", id)
                    bundle.putString("museumName", name)
                    navController.navigate(R.id.action_museumDetailsFragment_to_artWorksFragment, bundle)
                }

                CarouselSnapHelper().attachToRecyclerView(carouselRecyclerViewMuseumImages)
                CarouselSnapHelper().attachToRecyclerView(carouselRecyclerViewArtWorksImages)
                CarouselSnapHelper().attachToRecyclerView(carouselRecyclerViewEvents)

                buttonTicket.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("museumId", id)
                    bundle.putString("museumName", name)
                    navController.navigate(R.id.action_museumDetailsFragment_to_ticketFragment, bundle)
                }
            }
            fetchMuseumImagesData()
            fetchArtWorkImagesData()
            fetchEventsData()
        }
        private fun fetchMuseumImagesData() {
            db.collection("museums/$id/imagesCollectionMuseum")
                .get()
                .addOnSuccessListener { museumImages ->
                    for (museumImage in museumImages) {
                        val image = Image.fromSnapshot(museumImage.id, museumImage.data)
                        this.museumImagesList.add(image)
                    }
                    binding.carouselRecyclerViewMuseumImages.adapter = ImageAdapter(museumImagesList, requireContext())
                }
                .addOnFailureListener {
                    showToast("An error occurred: ${it.localizedMessage}", requireContext())
                }
        }
        private fun fetchArtWorkImagesData() {
            db.collection("museums/$id/imagesCollectionArtWork")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val image = Image.fromSnapshot(document.id, document.data)
                        this.artWorksList.add(image)
                    }
                    binding.carouselRecyclerViewArtWorksImages.adapter = ImageAdapter(artWorksList, requireContext())
                }
                .addOnFailureListener {
                    showToast("An error occurred: ${it.localizedMessage}", requireContext())
                }
        }
        private fun fetchEventsData() {
            db.collection("museums/$id/events")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val event = Event.fromSnapshot(document.id, document.data)
                        this.eventList.add(event)
                    }
                    binding.carouselRecyclerViewEvents.adapter = EventAdapter(eventList, requireContext()) {
                        val bundle = Bundle()
                        bundle.putString("eventId", it.id)
                        bundle.putString("eventTitle", it.title)
                        bundle.putString("eventDescription", it.description)
                        bundle.putString("eventPathToImage", it.pathToImage)
                        navController.navigate(R.id.action_museumDetailsFragment_to_eventDetailsFragment, bundle)
                    }
                }
                .addOnFailureListener {
                    showToast("An error occurred: ${it.localizedMessage}", requireContext())
                }
        }
    }