package com.example.museumexplore.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.R
import com.example.museumexplore.databinding.ArtWorksDisplayBinding
import com.example.museumexplore.databinding.FragmentArtWorksBinding
import com.example.museumexplore.modules.ArtWork
import com.example.museumexplore.modules.Category
import com.example.museumexplore.setImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class ArtWorksFragment : Fragment() {

    private var _binding: FragmentArtWorksBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    var artWorksList = arrayListOf<ArtWork>()
    private var artWorksAdapter = ArtWorksAdapter()

    private var categoriesList = arrayListOf<Category>()
    private var categoryMap = mapOf<String, String>() // Map for efficient category lookup

    private var museumId: String? = null
    private var museumName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""
        _binding = FragmentArtWorksBinding.inflate(inflater, container, false)
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
            museumName = bundle.getString("museumName")
        }

        navController = Navigation.findNavController(view)

        binding.textViewMuseumName.text = museumName
        binding.gridViewArtWorks.adapter = artWorksAdapter

        val appDatabase = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            if (appDatabase != null) {
                museumId?.let { currentMuseumId ->
                    val artWorksData = ArtWork.fetchArtWorksData(currentMuseumId)
                    for (artWorkData in artWorksData) {
                        appDatabase.artWorkDao().add(artWorkData)
                    }
                    val categoriesData = Category.fetchCategoriesData(currentMuseumId)
                    for (categoryData in categoriesData) {
                        appDatabase.categoryDao().add(categoryData)
                    }
                    appDatabase.artWorkDao().getAll(currentMuseumId)
                        .observe(viewLifecycleOwner) {
                            artWorksList = it as ArrayList<ArtWork>
                            artWorksAdapter.notifyDataSetChanged()
                        }
                    appDatabase.categoryDao().getAll(currentMuseumId)
                        .observe(viewLifecycleOwner) {
                            categoriesList = it as ArrayList<Category>
                            // Build the category map for efficient lookup
                            categoryMap =
                                categoriesList.associateBy(Category::id, Category::descritpion)
                        }
                }
            }
        }
    }

    inner class ArtWorksAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return artWorksList.size
        }

        override fun getItem(position: Int): Any {
            return artWorksList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rootView = ArtWorksDisplayBinding.inflate(layoutInflater)

            rootView.textViewArtWorkName.text = artWorksList[position].name

            // Look up category description using the categoryMap
            val categoryDescription = categoryMap[artWorksList[position].categoryId]
            rootView.textViewCategory.text = categoryDescription ?: ""

            setImage(
                artWorksList[position].pathToImage,
                rootView.imageViewArtWork,
                requireContext()
            )

            rootView.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("artWorkId", artWorksList[position].id)
                navController.navigate(
                    R.id.action_artWorksFragment_to_artWorkDetailsFragment,
                    bundle
                )
            }

            return rootView.root
        }
    }
}