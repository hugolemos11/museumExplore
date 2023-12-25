package com.example.museumexplore.ui.home

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentArtWorkDetailsBinding
import com.example.museumexplore.databinding.FragmentArtWorksBinding
import com.example.museumexplore.databinding.FragmentHomeBinding
import com.example.museumexplore.databinding.MuseumDisplayBinding
import com.example.museumexplore.modules.Museum
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ArtWorkDetailsFragment : Fragment() {

    private var _binding: FragmentArtWorkDetailsBinding? = null
    private val binding get() = _binding!!

    //private lateinit var navController: NavController

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*val artWorksName = intent.extras?.getString(EXTRA_NAME)
        val artistName = intent.extras?.getString(EXTRA_ARTIST)
        val year = intent.extras?.getInt(EXTRA_YEAR, 0)
        val category = intent.extras?.getString(EXTRA_CATEGORY)
        val description = intent.extras?.getString(EXTRA_DESCRIPTION)

        // arranjar uma forma melhor
        val drawableId = intent.extras?.getInt(EXTRA_IMAGE)?: 0
        val drawable = ContextCompat.getDrawable(this, drawableId)

        binding.imageViewArtWorkImage.setImageDrawable(drawable)
        binding.textViewArtWorkName.text = artWorksName
        binding.textViewArtistNameYear.text = artistName//"${artistName} ${year}"
        binding.textViewArtWorkCategory.text =  category
        binding.textViewArtWorkDescription.text = description*/


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
            textViewArtistNameYear.text = "$artistName $artWorkYear"
            textViewArtWorkCategory.text = artWorkCategory
            textViewArtWorkDescription.text = artWorkDescription
        }
    }

    companion object{
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_ARTIST = "extra_artist"
        const val EXTRA_YEAR = "extra_year"
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_DESCRIPTION = "extra_description"
    }
}