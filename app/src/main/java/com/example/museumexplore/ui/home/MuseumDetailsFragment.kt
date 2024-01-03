package com.example.museumexplore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentMuseumDetailsBinding
import com.example.museumexplore.modules.EventAdapter
import com.example.museumexplore.modules.Event
import com.example.museumexplore.modules.Image
import com.example.museumexplore.modules.ImageAdapter
import com.example.museumexplore.modules.Location
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap

class MuseumDetailsFragment : Fragment() {

    private var _binding: FragmentMuseumDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val museumImagesList = arrayListOf<Image>()
    private val artWorksList = arrayListOf<Image>()
    private val eventList = arrayListOf<Event>()
    private var id: String? = null
    private var museumName: String? = null
    private var museumDescription: String? = null
    private var museumRate: Int? = null
    private var museumPathToImage: String? = null

    private lateinit var mapBoxMap: MapboxMap

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
            museumName = bundle.getString("museumName")
            museumDescription = bundle.getString("museumDescription")
            museumRate = bundle.getInt("museumRate")
            museumPathToImage = bundle.getString("museumPathToImage")
        }

        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""

        navController = Navigation.findNavController(view)


        setImage(museumPathToImage, binding.museumImage, requireContext())


        binding.apply {
            textViewMuseumName.text = museumName
            textViewDescription.text = museumDescription

            buttonCollection.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("museumId", id)
                bundle.putString("museumName", museumName)
                navController.navigate(
                    R.id.action_museumDetailsFragment_to_artWorksFragment,
                    bundle
                )
            }

            CarouselSnapHelper().attachToRecyclerView(carouselRecyclerViewMuseumImages)
            CarouselSnapHelper().attachToRecyclerView(carouselRecyclerViewArtWorksImages)
            CarouselSnapHelper().attachToRecyclerView(carouselRecyclerViewEvents)

            buttonTicket.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("museumId", id)
                bundle.putString("museumName", museumName)
                navController.navigate(R.id.action_museumDetailsFragment_to_ticketFragment, bundle)
            }
        }
        fetchMuseumImagesData()
        fetchArtWorkImagesData()
        fetchEventsData()
        fetchLocationData()
    }

    private fun fetchMuseumImagesData() {
        db.collection("museums/$id/imagesCollectionMuseum")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val image = Image.fromSnapshot(document.id, document.data)
                    this.museumImagesList.add(image)
                }
                binding.carouselRecyclerViewMuseumImages.adapter =
                    ImageAdapter(museumImagesList, requireContext())
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
                binding.carouselRecyclerViewArtWorksImages.adapter =
                    ImageAdapter(artWorksList, requireContext())
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
                binding.carouselRecyclerViewEvents.adapter =
                    EventAdapter(eventList, requireContext()) {
                        val bundle = Bundle()
                        bundle.putString("eventId", it.id)
                        bundle.putString("eventTitle", it.title)
                        bundle.putString("eventDescription", it.description)
                        bundle.putString("eventPathToImage", it.pathToImage)
                        navController.navigate(
                            R.id.action_museumDetailsFragment_to_eventDetailsFragment,
                            bundle
                        )
                    }
            }
            .addOnFailureListener {
                showToast("An error occurred: ${it.localizedMessage}", requireContext())
            }
    }

    private fun fetchLocationData() {
        db.collection("museums/$id/location")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.first()
                    val location = Location.fromSnapshot(document.id, document.data)
                    configMap(location)
                } else {
                    showToast("No document found", requireContext())
                }
            }
            .addOnFailureListener {
                showToast("An error occurred: ${it.localizedMessage}", requireContext())
            }
    }

    private fun configMap(location: Location) {
        val mapView = binding.mapView

        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(location.longitude, location.latitude))
                .pitch(3.0)
                .zoom(12.0)
                .bearing(0.0)
                .build()
        )
    }
}