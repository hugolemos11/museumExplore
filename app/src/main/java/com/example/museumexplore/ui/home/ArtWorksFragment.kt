package com.example.museumexplore.ui.home

import android.graphics.BitmapFactory
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
import com.example.museumexplore.databinding.MuseumDisplayBinding
import com.example.museumexplore.modules.ArtWorks
import com.example.museumexplore.modules.Museum
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class ArtWorksFragment : Fragment() {

    private var _binding: FragmentArtWorksBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    var artWorksList = arrayListOf<ArtWorks>()
    private var  adapter = ArtWorksAdapter()

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

        navController = Navigation.findNavController(view);

        binding.textViewMuseumName.text = museumName
        binding.gridViewArtWorks.adapter = adapter

        val db = Firebase.firestore
        db.collection("museums/$museumId/artWorks")
            .addSnapshotListener { snapshot, error ->
                snapshot?.documents?.let {
                    this.artWorksList.clear()
                    for (document in it) {
                        document.data?.let{ data ->
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

        //artWorksList.add(ArtWorks("aa", "ArtWork1", "Picasso", 1986 ,"Cubism", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum." , "artWorksImages/art_work1.jpg"))
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

            artWorksList[position].pathToImage?.let {
                val storage = Firebase.storage
                val storageRef = storage.reference
                val pathReference = storageRef.child(it)
                val ONE_MEGABYTE: Long = 10 * 1024 * 1024
                pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { data ->
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.count())
                    rootView.imageViewArtWork.setImageBitmap(bitmap)
                }.addOnFailureListener {
                    // Handle any errors
                }

            }

            rootView.root.setOnClickListener{
                val bundle = Bundle()
                bundle.putString("artWorkId", artWorksList[position].id)
                bundle.putString("artWorkName", artWorksList[position].name)
                bundle.putString("artistName", artWorksList[position].artist)
                bundle.putString("artWorkDescription", artWorksList[position].description)
                bundle.putString("artWorkCategory", artWorksList[position].category)
                bundle.putInt("artWorkYear", artWorksList[position].year)
                bundle.putString("artWorkPathToImage", artWorksList[position].pathToImage)
                navController.navigate(R.id.action_artWorksFragment_to_artWorkDetailsFragment, bundle)
            }

            return rootView.root
        }
    }

}