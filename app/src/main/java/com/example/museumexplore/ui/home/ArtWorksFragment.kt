package com.example.museumexplore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.R
import com.example.museumexplore.databinding.ArtWorksDisplayBinding
import com.example.museumexplore.databinding.FragmentArtWorksBinding
import com.example.museumexplore.modules.ArtWorks
import com.example.museumexplore.setImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ArtWorksFragment : Fragment() {

    private var _binding: FragmentArtWorksBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    var artWorksList = arrayListOf<ArtWorks>()
    private var adapter = ArtWorksAdapter()

    private var museumId: String? = null
    private var museumName: String? = null

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

        arguments?.let { bundle ->
            museumId = bundle.getString("museumId")
            museumName = bundle.getString("museumName")
        }

        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""

        navController = Navigation.findNavController(view)

        binding.textViewMuseumName.text = museumName
        binding.gridViewArtWorks.adapter = adapter

        val db = Firebase.firestore
        db.collection("museums/$museumId/artWorks")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.documents?.let {
                    this.artWorksList.clear()
                    for (document in it) {
                        document.data?.let { data ->
                            this.artWorksList.add(
                                ArtWorks.fromSnapshot(
                                    document.id,
                                    data
                                )
                            )
                        }
                    }
                    this.adapter.notifyDataSetChanged()
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

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rootView = ArtWorksDisplayBinding.inflate(layoutInflater)

            rootView.textViewArtWorkName.text = artWorksList[position].name
            rootView.textViewCategory.text = artWorksList[position].category

            setImage(
                artWorksList[position].pathToImage,
                rootView.imageViewArtWork,
                requireContext()
            )

            rootView.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("artWorkId", artWorksList[position].id)
                bundle.putString("artWorkName", artWorksList[position].name)
                bundle.putString("artistName", artWorksList[position].artist)
                bundle.putString("artWorkDescription", artWorksList[position].description)
                bundle.putString("artWorkCategory", artWorksList[position].category)
                bundle.putInt("artWorkYear", artWorksList[position].year)
                bundle.putString("artWorkPathToImage", artWorksList[position].pathToImage)
                navController.navigate(
                    R.id.action_artWorksFragment_to_artWorkDetailsFragment,
                    bundle
                )
            }

            return rootView.root
        }
    }

}