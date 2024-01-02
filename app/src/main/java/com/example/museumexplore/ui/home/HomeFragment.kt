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
import com.bumptech.glide.Glide
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentHomeBinding
import com.example.museumexplore.databinding.MuseumDisplayBinding
import com.example.museumexplore.modules.Image
import com.example.museumexplore.modules.ImageAdapter
import com.example.museumexplore.modules.Museum
import com.example.museumexplore.showToast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    var museums = arrayListOf<Museum>()
    private  var  adapter = MuseumAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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

        navController = Navigation.findNavController(view)

        binding.gridViewMuseums.adapter = adapter

        val db = Firebase.firestore
        db.collection("museums")
            .addSnapshotListener { snapshot, _ ->
                snapshot?.documents?.let {
                    this.museums.clear()
                    for (document in it) {
                        document.data?.let{ data ->
                            this.museums.add(
                                Museum.fromSnapshot(
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

    inner class MuseumAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return museums.size
        }

        override fun getItem(position: Int): Any {
            return museums[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rootView = MuseumDisplayBinding.inflate(layoutInflater)

            rootView.textViewMuseumName.text = museums[position].name

            museums[position].pathToImage?.let {
                val storage = Firebase.storage
                val storageRef = storage.reference
                val pathReference = storageRef.child(it)
                /*val ONE_MEGABYTE: Long = 10 * 1024 * 1024
                pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { data ->
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.count())
                    rootView.imageView3.setImageBitmap(bitmap)
                }.addOnFailureListener {
                    // Handle any errors
                }*/
                pathReference.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(requireContext())
                        .load(uri)
                        .into(rootView.imageView3)
                }.addOnFailureListener {
                    // Handle any errors
                }
            }

            rootView.root.setOnClickListener{
                val bundle = Bundle()
                bundle.putString("museumId", museums[position].id)
                bundle.putString("museumName", museums[position].name)
                bundle.putString("museumDescription", museums[position].description)
                bundle.putInt("museumRate", museums[position].rate)
                bundle.putString("museumPathToImage", museums[position].pathToImage)
                navController.navigate(R.id.action_homeFragment_to_museumDetailsFragment, bundle)
            }

            return rootView.root
        }

    }
}