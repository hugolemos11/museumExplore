package com.example.museumexplore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.museumexplore.R
import com.example.museumexplore.databinding.ArtWorksDisplayBinding
import com.example.museumexplore.databinding.FragmentArtWorksBinding
import com.example.museumexplore.databinding.FragmentHomeBinding
import com.example.museumexplore.databinding.MuseumDisplayBinding
import com.example.museumexplore.modules.ArtWorks
import com.example.museumexplore.modules.Museum


class ArtWorksFragment : Fragment() {



    private var _binding: FragmentArtWorksBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    var artWorks = arrayListOf<ArtWorks>()
    private  var  adapter = ArtWorksAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtWorksBinding.inflate(inflater, container, false)
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

        navController = Navigation.findNavController(view);

        artWorks.add(ArtWorks("aa", "ArtWork1", "Picasso", 1986 ,"Cubism", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum." ,R.drawable.art_work1))
        artWorks.add(ArtWorks("ab", "ArtWork2", "Picasso", 1986 ,"Cubism", "",R.drawable.art_work2))
        artWorks.add(ArtWorks("ac", "ArtWork3", "Picasso", 1986 ,"Cubism", "",R.drawable.art_work3))
        artWorks.add(ArtWorks("ba", "ArtWork4", "Picasso", 1986 ,"Cubism", "",R.drawable.art_work4))
        artWorks.add(ArtWorks("bb", "ArtWork5", "Picasso", 1986 ,"Cubism", "",R.drawable.art_work1))
        artWorks.add(ArtWorks("bc", "ArtWork6", "Picasso", 1986 ,"Cubism", "",R.drawable.art_work2))

        binding.gridViewMuseums.adapter = adapter

    }

    inner class ArtWorksAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return artWorks.size
        }

        override fun getItem(position: Int): Any {
            return artWorks[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rootView = ArtWorksDisplayBinding.inflate(layoutInflater)

            val drawableId = artWorks[position].image
            val drawable = ContextCompat.getDrawable(requireContext(), drawableId)

            rootView.imageViewArtWork.setImageDrawable(drawable)
            rootView.textViewArtWorkName.text = artWorks[position].artWorkName
            rootView.textViewCategory.text = artWorks[position].category

            rootView.root.setOnClickListener{
                navController.navigate(R.id.action_artWorksFragment_to_artWorkDetailsFragment)
            }

            return rootView.root
        }

    }
}