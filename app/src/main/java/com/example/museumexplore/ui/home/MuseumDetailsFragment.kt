package com.example.museumexplore.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentMuseumDetailsBinding
import com.example.museumexplore.modules.EventAdapter
import com.example.museumexplore.modules.Event
import com.example.museumexplore.modules.Image
import com.example.museumexplore.modules.ImageAdapter
import com.example.museumexplore.modules.Location
import com.example.museumexplore.modules.Museum
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.plugin.gestures.gestures

class MuseumDetailsFragment : Fragment() {

    private var _binding: FragmentMuseumDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private var museumImagesList = arrayListOf<Image>()
    private var artWorksList = arrayListOf<Image>()
    private val eventList = arrayListOf<Event>()
    private var museumId: String? = null
    private var museum: Museum? = null
//    private var museumName: String? = null
//    private var museumDescription: String? = null
//    private var museumRate: Int? = null
//    private var museumLongitude: Double? = null
//    private var museumLatitude: Double? = null
//    private var museumPathToImage: String? = null

    private lateinit var museumImagesAdapter: ImageAdapter
    private lateinit var artWorksImagesAdapter: ImageAdapter

    private lateinit var mapBoxMap: MapboxMap

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""
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
            museumId = bundle.getString("museumId")
//            museumName = bundle.getString("museumName")
//            museumDescription = bundle.getString("museumDescription")
//            museumRate = bundle.getInt("museumRate")
//            museumLongitude = bundle.getDouble("museumLongitude")
//            museumLatitude = bundle.getDouble("museumLatitude")
//            museumPathToImage = bundle.getString("museumPathToImage")
        }

        navController = Navigation.findNavController(view)



        val appDatabase = AppDatabase.getInstance(requireContext())

        if (appDatabase != null) {
            museumId?.let {
                museum = appDatabase.museumDao().get(it)
                Image.fetchMuseumImagesData(it) { museumImagesData ->
                    for (museumImageData in museumImagesData) {
                        appDatabase.imageDao().add(museumImageData)
                    }
                }
                Image.fetchArtWorksImagesData(it) { artWorksImagesData ->
                    for (artWorkImageData in artWorksImagesData) {
                        appDatabase.imageDao().add(artWorkImageData)
                    }
                }
            }
            appDatabase.imageDao().getAllMuseumImages().observe(viewLifecycleOwner, Observer {
                museumImagesList = it as ArrayList<Image>
                museumImagesAdapter = ImageAdapter(museumImagesList, requireContext())
                binding.carouselRecyclerViewMuseumImages.adapter = museumImagesAdapter
            })
            appDatabase.imageDao().getAllArtWorksImages().observe(viewLifecycleOwner, Observer {
                artWorksList = it as ArrayList<Image>
                artWorksImagesAdapter = ImageAdapter(artWorksList, requireContext())
                binding.carouselRecyclerViewArtWorksImages.adapter = artWorksImagesAdapter
            })

        }
        if (museum != null) {
            setImage(museum!!.pathToImage, binding.museumImage, requireContext())


            binding.apply {
                textViewMuseumName.text = museum!!.name
                textViewDescription.text = museum!!.description

                buttonCollection.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("museumId", museumId)
                    bundle.putString("museumName", museum!!.name)
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
                    bundle.putString("museumId", museumId)
                    bundle.putString("museumName", museum!!.name)
                    navController.navigate(
                        R.id.action_museumDetailsFragment_to_ticketFragment,
                        bundle
                    )
                }

                mapView.gestures.pitchEnabled = false
                mapView.gestures.scrollEnabled = false
                mapView.gestures.pinchToZoomEnabled = false
                mapView.gestures.rotateEnabled = false

                textView4.setOnClickListener {
                    val gmmIntentUri =
                        Uri.parse("google.navigation:q=${museum!!.location["latitude"]},${museum!!.location["longitude"]}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    startActivity(mapIntent)
                }
            }
            fetchEventsData()
            configMap()
        }


    }

    private fun fetchEventsData() {
        db.collection("events")
            .whereEqualTo("museumId", museumId)
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

    private fun configMap() {
        val mapView = binding.mapView

        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(museum!!.location["longitude"]?.let {
                    museum!!.location["latitude"]?.let { it1 ->
                        Point.fromLngLat(
                            it,
                            it1
                        )
                    }
                })
                .pitch(3.0)
                .zoom(12.0)
                .bearing(0.0)
                .build()
        )
    }
}