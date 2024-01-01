package com.example.museumexplore.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.museumexplore.databinding.FragmentEventDetailsBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class EventDetailsFragment : Fragment() {

    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!

    private var id : String? = null
    private var eventTitle : String? = null
    private var eventDescription : String? = null
    private var eventPathToImage : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""

        //navController = Navigation.findNavController(view);

        arguments?.let { bundle ->
            id = bundle.getString("eventId")
            eventTitle = bundle.getString("eventTitle")
            eventDescription = bundle.getString("eventDescription")
            eventPathToImage = bundle.getString("eventPathToImage")
        }

        eventPathToImage?.let { imagePath ->
            // Load the image from Firebase Storage
            val storage = Firebase.storage
            val storageRef = storage.reference
            val pathReference = storageRef.child(imagePath)
            val ONE_MEGABYTE: Long = 10 * 1024 * 1024
            pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { data ->
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.count())
                binding.imageViewEventDetails.setImageBitmap(bitmap)
            }.addOnFailureListener {
                // Handle any errors
                Log.e("MuseumDetailsFragment", "Failed to load image from Firebase Storage")
            }
        }

        binding.apply {
            textViewEventTitleDetails.text = eventTitle
            textViewEventDescriptionDetails.text = eventDescription
        }
    }
}