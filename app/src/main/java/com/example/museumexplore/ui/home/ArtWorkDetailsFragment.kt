package com.example.museumexplore.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.renderscript.ScriptGroup.Binding
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentArtWorkDetailsBinding
import com.example.museumexplore.modules.ArtWork
import com.example.museumexplore.modules.Category
import com.example.museumexplore.modules.EventAdapter
import com.example.museumexplore.modules.ImageArtWork
import com.example.museumexplore.modules.ImageArtWorkAdapter
import com.example.museumexplore.modules.Museum
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import kotlinx.coroutines.launch
import java.util.Locale

class ArtWorkDetailsFragment : Fragment() {

    private var _binding: FragmentArtWorkDetailsBinding? = null
    private val binding get() = _binding!!

    private var artWorkImagesList = arrayListOf<ImageArtWork>()

    // slider Images
    private lateinit var viewPager2 : ViewPager2
    private lateinit var pageChangeListener : ViewPager2.OnPageChangeCallback
    private lateinit var imageArtWorkAdapter: ImageArtWorkAdapter

    private lateinit var slideDot : LinearLayout

    private val params = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ).apply {
        setMargins(8,0,8,0)
    }

    private var id: String? = null
    private var artWork: ArtWork? = null
    private var category: Category? = null

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
        viewPager2.unregisterOnPageChangeCallback(pageChangeListener)
        releaseTextToSpeech()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            id = bundle.getString("artWorkId")
        }

        viewPager2 = binding.artWorkSliderImages
        slideDot = binding.slideDot

        imageArtWorkAdapter = ImageArtWorkAdapter()

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

                    val artWorkImagesData = ImageArtWork.fetchArtWorksImagesData(currentArtWorkId)
                    if (artWorkImagesData.isNotEmpty()){
                        appDatabase.imageArtWorkDao().delete()
                        for (artWorkImageData in artWorkImagesData) {
                            appDatabase.imageArtWorkDao().add(artWorkImageData)
                        }
                    }


                    appDatabase.imageArtWorkDao().getAll(currentArtWorkId).observe(viewLifecycleOwner) {
                        artWorkImagesList = arrayListOf()
                        artWorkImagesList.add(ImageArtWork("","", artWork!!.pathToImage))
                        for (imageArtWork in it) {
                            artWorkImagesList.add(imageArtWork)
                        }

                        viewPager2.adapter = imageArtWorkAdapter
                        imageArtWorkAdapter.submitList(artWorkImagesList)

                        slideDot.removeAllViews()

                        val dotsImage = Array(artWorkImagesList.size) { ImageView(requireContext()) }

                        dotsImage.forEach {currentDot ->
                            currentDot.setImageResource(
                                R.drawable.non_active_dot
                            )
                            slideDot.addView(currentDot,params)
                        }

                        // default first dot selected
                        dotsImage[0].setImageResource(R.drawable.active_dot)

                        pageChangeListener = object : ViewPager2.OnPageChangeCallback(){
                            override fun onPageSelected(position: Int) {
                                dotsImage.mapIndexed { index, imageView ->
                                    if (position == index){
                                        imageView.setImageResource(
                                            R.drawable.active_dot
                                        )
                                    }else{
                                        imageView.setImageResource(R.drawable.non_active_dot)
                                    }
                                }
                                super.onPageSelected(position)
                            }
                        }
                        viewPager2.registerOnPageChangeCallback(pageChangeListener)
                    }
                }
            }

            artWork?.let { currentArtWork ->

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

    private fun releaseTextToSpeech() {
        textToSpeech.stop()
        textToSpeech.shutdown()
    }
}