package com.example.museumexplore.ui.home

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.museumexplore.databinding.FragmentArtWorkDetailsBinding
import com.example.museumexplore.modules.ArtWork
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale

class ArtWorkDetailsFragment : Fragment() {

    private var _binding: FragmentArtWorkDetailsBinding? = null
    private val binding get() = _binding!!

    private var id: String? = null
    private var artWork: ArtWork? = null
//    private var artworkName: String? = null
//    private var artistName: String? = null
//    private var artWorkDescription: String? = null
//    private var artWorkCategory: String? = null
//    private var artWorkYear: Int? = null
//    private var artWorkPathToImage: String? = null

    private val db = Firebase.firestore

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""
        _binding = FragmentArtWorkDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            id = bundle.getString("artWorkId")
        }

        fetchArtWorkData();

        configTextToSpeech()
    }

    private fun fetchArtWorkData() {
        db.collection("artWorks")
            .document("$id")
            .get()
            .addOnSuccessListener { documentSnapshot ->

                if (documentSnapshot.exists()) {
                    artWork =
                        documentSnapshot.data?.let {
                            ArtWork.fromSnapshot(
                                documentSnapshot.id,
                                it
                            )
                        }
                    setImage(artWork?.pathToImage, binding.imageViewArtWorkImage, requireContext())

                    binding.apply {

                        textViewArtWorkName.text = artWork?.name
                        textViewArtistNameYear.text = "${artWork?.artist}, ${artWork?.year}"
                        textViewArtWorkCategory.text = artWork?.categoryId
                        textViewArtWorkDescription.text = artWork?.description

                    }
                } else {
                    showToast("Artwork with id $id not found", requireContext())
                }
            }
            .addOnFailureListener {
                showToast("An error occurred: ${it.localizedMessage}", requireContext())
            }
    }

    private fun configTextToSpeech() {
        val artWorkName = "Art Work: ${binding.textViewArtWorkName.text}".trim()
        val artistAndYear = "Artist and Year: ${binding.textViewArtistNameYear.text}".trim()
        val description = "Description: ${binding.textViewArtWorkDescription.text}".trim()

        val textToRead = "$artWorkName. $artistAndYear. $description"

        textToSpeech = TextToSpeech(requireContext()) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    showToast("Language not supported", requireContext())
                }
            }
        }

        binding.imageViewPlayIcon.setOnClickListener {
            textToSpeech.speak(
                textToRead,
                TextToSpeech.QUEUE_FLUSH,
                null,
                null
            )
        }
    }
}