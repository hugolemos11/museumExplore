package com.example.museumexplore.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.databinding.FragmentArtWorkDetailsBinding
import com.example.museumexplore.modules.ArtWork
import com.example.museumexplore.modules.Category
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.Locale

class ArtWorkDetailsFragment : Fragment() {

    private var _binding: FragmentArtWorkDetailsBinding? = null
    private val binding get() = _binding!!

    private var id: String? = null
    private var artWork: ArtWork? = null
    private var category: Category? = null

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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            id = bundle.getString("artWorkId")
        }

        val appDatabase = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            if (appDatabase != null) {
                id?.let { currentArtWorkId ->
                    val artWorkData = ArtWork.fetchArtWorkData(currentArtWorkId)
                    appDatabase.artWorkDao().add(artWorkData)
                    artWork = appDatabase.artWorkDao().get(currentArtWorkId)

                    artWork?.let { currentArtWork ->
                        val categoryData = Category.fetchCategoryData(currentArtWork.categoryId)
                        appDatabase.categoryDao().add(categoryData)
                        category = appDatabase.categoryDao().get(currentArtWork.categoryId)
                    }
                }
            }

            artWork?.let { currentArtWork ->
                setImage(
                    currentArtWork.pathToImage,
                    binding.imageViewArtWorkImage,
                    requireContext()
                )

                binding.apply {
                    textViewArtWorkName.text = currentArtWork.name
                    textViewArtistNameYear.text = "${currentArtWork.artist}, ${currentArtWork.year}"
                    textViewArtWorkDescription.text = currentArtWork.description

                    category?.let { currentCategory ->
                        textViewArtWorkCategory.text = currentCategory.descritpion
                    }
                }

                configTextToSpeech()
            }
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