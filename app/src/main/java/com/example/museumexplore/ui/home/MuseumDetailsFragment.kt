package com.example.museumexplore.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentMuseumDetailsBinding
import com.example.museumexplore.modules.EventAdapter
import com.example.museumexplore.modules.Event
import com.example.museumexplore.modules.Image
import com.example.museumexplore.modules.ImageAdapter
import com.example.museumexplore.modules.Museum
import com.example.museumexplore.setImage
import com.google.android.material.carousel.CarouselSnapHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import kotlinx.coroutines.launch

class MuseumDetailsFragment : Fragment() {

    private var _binding: FragmentMuseumDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth

    private var museumImagesList = arrayListOf<Image>()
    private var artWorksList = arrayListOf<Image>()
    private var eventList = arrayListOf<Event>()
    private var museumId: String? = null
    private var museum: Museum? = null

    private lateinit var museumImagesAdapter: ImageAdapter
    private lateinit var artWorksImagesAdapter: ImageAdapter
    private lateinit var eventsAdapter: EventAdapter

    private var mapView: MapView? = null

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
            museumId = bundle.getString("museumId")
        }

        navController = Navigation.findNavController(view)

        auth = Firebase.auth

        val appDatabase = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            if (appDatabase != null) {
                museumId?.let { currentMuseumId ->
                    museum = appDatabase.museumDao().get(currentMuseumId)
                    val museumImagesData = Image.fetchMuseumImagesData(currentMuseumId)
                    val artWorksImagesData = Image.fetchArtWorksImagesData(currentMuseumId)
                    if (museumImagesData.isNotEmpty() && artWorksImagesData.isNotEmpty()){
                        appDatabase.imageDao().delete()
                        for (museumImageData in museumImagesData) {
                            appDatabase.imageDao().add(museumImageData)
                        }
                        for (artWorkImageData in artWorksImagesData) {
                            appDatabase.imageDao().add(artWorkImageData)
                        }
                    }

                    val eventsData = Event.fetchEventsData(currentMuseumId)
                    if (eventsData.isNotEmpty()) {
                        appDatabase.eventDao().delete()
                        for (eventData in eventsData) {
                            appDatabase.eventDao().add(eventData)
                        }
                    }

                    appDatabase.imageDao().getAllMuseumImages(currentMuseumId)
                        .observe(viewLifecycleOwner) {
                            museumImagesList = it as ArrayList<Image>
                            museumImagesAdapter = ImageAdapter(museumImagesList, requireContext())
                            binding.carouselRecyclerViewMuseumImages.adapter = museumImagesAdapter
                        }
                    appDatabase.imageDao().getAllArtWorksImages(currentMuseumId)
                        .observe(viewLifecycleOwner) {
                            artWorksList = it as ArrayList<Image>
                            artWorksImagesAdapter = ImageAdapter(artWorksList, requireContext())
                            binding.carouselRecyclerViewArtWorksImages.adapter =
                                artWorksImagesAdapter
                        }
                    appDatabase.eventDao().getAll(currentMuseumId)
                        .observe(viewLifecycleOwner) {
                            eventList = it as ArrayList<Event>
                            eventsAdapter = EventAdapter(eventList, requireContext()) { event ->
                                val bundle = Bundle()
                                bundle.putString("eventId", event.id)
                                navController.navigate(
                                    R.id.action_museumDetailsFragment_to_eventDetailsFragment,
                                    bundle
                                )
                            }
                            binding.carouselRecyclerViewEvents.adapter = eventsAdapter
                        }
                }


                museum?.let { currentMuseum ->
                    setImage(currentMuseum.pathToImage, binding.museumImage, requireContext())

                    binding.apply {
                        textViewMuseumName.text = currentMuseum.name
                        textViewDescription.text = currentMuseum.description

                        buttonCollection.setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("museumId", museumId)
                            bundle.putString("museumName", currentMuseum.name)
                            navController.navigate(
                                R.id.action_museumDetailsFragment_to_artWorksFragment,
                                bundle
                            )
                        }

                        CarouselSnapHelper().attachToRecyclerView(carouselRecyclerViewMuseumImages)
                        CarouselSnapHelper().attachToRecyclerView(carouselRecyclerViewArtWorksImages)
                        CarouselSnapHelper().attachToRecyclerView(carouselRecyclerViewEvents)

                        buttonTicket.setOnClickListener {
                            if (auth.uid != null) {
                                val bundle = Bundle()
                                bundle.putString("museumId", museumId)
                                bundle.putString("museumName", currentMuseum.name)
                                navController.navigate(
                                    R.id.action_museumDetailsFragment_to_ticketFragment,
                                    bundle
                                )
                            } else {
                                navController.navigate(R.id.action_homeNavigation_to_autenticationNavigation)
                            }
                        }

                        mapView.gestures.pitchEnabled = false
                        mapView.gestures.scrollEnabled = false
                        mapView.gestures.pinchToZoomEnabled = false
                        mapView.gestures.rotateEnabled = false
                        mapView.gestures.doubleTapToZoomInEnabled = false
                        mapView.gestures.doubleTouchToZoomOutEnabled = false

                        configMap(currentMuseum)
                    }
                }
            }
        }
    }

    private fun configMap(museum: Museum) {
        mapView = binding.mapView

        mapView?.mapboxMap?.loadStyle(
            Style.SATELLITE
        ) { addAnnotationToMap(museum) }

        mapView?.mapboxMap?.setCamera(
            CameraOptions.Builder()
                .center(museum.location["longitude"]?.let { longitude ->
                    museum.location["latitude"]?.let { latitude ->
                        Point.fromLngLat(
                            longitude,
                            latitude
                        )
                    }

                })
                .pitch(3.0)
                .zoom(15.0)
                .bearing(0.0)
                .build()
        )
    }

    private fun addAnnotationToMap(museum: Museum) {
        // Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            requireContext(),
            R.drawable.red_marker
        )?.let { iconBitmap ->
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager()

            museum.location["longitude"]?.let { longitude ->
                museum.location["latitude"]?.let { latitude ->
                    val coordinates = Point.fromLngLat(longitude, latitude)

                    // Set options for the resulting symbol layer.
                    val pointAnnotationOptions = PointAnnotationOptions()
                        // Define a geographic coordinate.
                        .withPoint(coordinates)
                        // Specify the bitmap you assigned to the point annotation
                        // The bitmap will be added to map style automatically.
                        .withIconImage(iconBitmap)

                    // Add the resulting pointAnnotation to the map.
                    val annotation = pointAnnotationManager?.create(pointAnnotationOptions)

                    // Set up a click listener for the annotation
                    annotation?.let { pointAnnotation ->
                        pointAnnotationManager.clickListeners.add { clickedAnnotation ->
                            if (clickedAnnotation == pointAnnotation) {
                                // Handle the click event
                                val gmmIntentUri =
                                    Uri.parse("google.navigation:q=${coordinates.latitude()},${coordinates.longitude()}&mode=d")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                startActivity(mapIntent)
                                true
                            } else {
                                false
                            }
                        }
                    }
                }
            }
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}