package com.example.museumexplore.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.museumexplore.databinding.FragmentArtWorkDetailsBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ArtWorkDetailsFragment : Fragment() {

    private var _binding: FragmentArtWorkDetailsBinding? = null
    private val binding get() = _binding!!

    private var id : String? = null
    private var artworkName : String? = null
    private var artistName : String? = null
    private var artWorkDescription : String? = null
    private var artWorkCategory : String? = null
    private var artWorkYear : Int? = null
    private var artWorkPathToImage : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtWorkDetailsBinding.inflate(inflater, container, false)
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
            id = bundle.getString("artWorkId")
            artworkName = bundle.getString("artWorkName")
            artistName = bundle.getString("artistName")
            artWorkDescription = bundle.getString("artWorkDescription")
            artWorkCategory = bundle.getString("artWorkCategory")
            artWorkYear = bundle.getInt("artWorkYear")
            artWorkPathToImage = bundle.getString("artWorkPathToImage")
        }

        artWorkPathToImage?.let { imagePath ->
            // Load the image from Firebase Storage
            val storage = Firebase.storage
            val storageRef = storage.reference
            val pathReference = storageRef.child(imagePath)
            val ONE_MEGABYTE: Long = 10 * 1024 * 1024
            pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { data ->
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.count())
                binding.imageViewArtWorkImage.setImageBitmap(bitmap)
            }.addOnFailureListener {
                // Handle any errors
                Log.e("MuseumDetailsFragment", "Failed to load image from Firebase Storage")
            }
        }

        binding.apply {
            imageViewArtWorkImage
            textViewArtWorkName.text = artworkName
            textViewArtistNameYear.text = "$artistName, $artWorkYear"
            textViewArtWorkCategory.text = artWorkCategory
            textViewArtWorkDescription.text = artWorkDescription
        }
    }
}