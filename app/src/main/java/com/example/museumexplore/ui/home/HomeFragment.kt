package com.example.museumexplore.ui.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentHomeBinding
import com.example.museumexplore.databinding.MuseumDisplayBinding
import com.example.museumexplore.modules.Museum
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Locale


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private var museumsList = arrayListOf<Museum>()

    private val db = Firebase.firestore

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

        fetchMuseumsData()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterMuseums(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterMuseums(newText)
                return true
            }

        })
    }

    private fun fetchMuseumsData() {
        db.collection("museums")
            .get()
            .addOnSuccessListener { documents ->
                // clear de List for don't duplicate de data
                museumsList.clear()

                for (document in documents) {
                    val museum = Museum.fromSnapshot(document.id, document.data)
                    this.museumsList.add(museum)
                }
                binding.gridViewMuseums.adapter =
                    MuseumAdapter()

                MuseumAdapter().notifyDataSetChanged()
            }
            .addOnFailureListener {
                showToast("An error occurred: ${it.localizedMessage}", requireContext())
            }
    }

    private fun filterMuseums(query: String?) {
        if (query != null) {
            db.collection("museums")
                .whereGreaterThanOrEqualTo("nameSearch", query)
                .whereLessThanOrEqualTo("nameSearch", query + '\uf8ff')
                .get()
                .addOnSuccessListener { documents ->
                    // clear de List for don't duplicate de data
                    museumsList.clear()

                    for (document in documents) {
                        val museum = Museum.fromSnapshot(document.id, document.data)
                        this.museumsList.add(museum)
                    }
                    binding.gridViewMuseums.adapter =
                        MuseumAdapter()

                    MuseumAdapter().notifyDataSetChanged()
                }
                .addOnFailureListener {
                    showToast("An error occurred: ${it.localizedMessage}", requireContext())
                }
        }
    }

    inner class MuseumAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return museumsList.size
        }

        override fun getItem(position: Int): Any {
            return museumsList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rootView = MuseumDisplayBinding.inflate(layoutInflater)

            rootView.textViewMuseumName.text = museumsList[position].name

            setImage(museumsList[position].pathToImage, rootView.imageView3, requireContext())

            rootView.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("museumId", museumsList[position].id)
                bundle.putString("museumName", museumsList[position].name)
                bundle.putString("museumDescription", museumsList[position].description)
                bundle.putInt("museumRate", museumsList[position].rate)
                bundle.putDouble("museumLongitude", museumsList[position].longitude)
                bundle.putDouble("museumLatitude", museumsList[position].latitude)
                bundle.putString("museumPathToImage", museumsList[position].pathToImage)
                navController.navigate(R.id.action_homeFragment_to_museumDetailsFragment, bundle)
            }

            return rootView.root
        }

    }
}