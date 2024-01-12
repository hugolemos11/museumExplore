package com.example.museumexplore.ui.home

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.museumexplore.databinding.FragmentArtWorkDetailsBinding
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import java.util.Locale

class ArtWorkDetailsFragment : Fragment() {

    private var _binding: FragmentArtWorkDetailsBinding? = null
    private val binding get() = _binding!!

    private var id: String? = null
    private var artworkName: String? = null
    private var artistName: String? = null
    private var artWorkDescription: String? = null
    private var artWorkCategory: String? = null
    private var artWorkYear: Int? = null
    private var artWorkPathToImage: String? = null

    private lateinit var textToSpeech: TextToSpeech

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

        arguments?.let { bundle ->
            id = bundle.getString("artWorkId")
            artworkName = bundle.getString("artWorkName")
            artistName = bundle.getString("artistName")
            artWorkDescription = bundle.getString("artWorkDescription")
            artWorkCategory = bundle.getString("artWorkCategory")
            artWorkYear = bundle.getInt("artWorkYear")
            artWorkPathToImage = bundle.getString("artWorkPathToImage")
        }

        setImage(artWorkPathToImage, binding.imageViewArtWorkImage, requireContext())

        binding.apply {
            textViewArtWorkName.text = artworkName
            textViewArtistNameYear.text = "$artistName, $artWorkYear"
            textViewArtWorkCategory.text = artWorkCategory
            textViewArtWorkDescription.text = artWorkDescription
        }

        configTextToSpeech()
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