package com.example.museumexplore.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentHomeBinding
import com.example.museumexplore.databinding.MuseumDisplayBinding
import com.example.museumexplore.modules.Museum
import com.example.museumexplore.setImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import androidx.lifecycle.Observer


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private var museumsList: List<Museum> = arrayListOf()

    private val museumAdapter = MuseumAdapter()

    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.gridViewMuseums.adapter = museumAdapter

        val appDatabase = AppDatabase.getInstance(requireContext())
        if (appDatabase != null) {
            Museum.fetchMuseumsData { museumsData ->
                for (museumData in museumsData) {
                    appDatabase.museumDao().add(museumData)
                }
            }
            appDatabase.museumDao().getAll().observe(viewLifecycleOwner, Observer {
                museumsList = it
                museumAdapter.notifyDataSetChanged()
            })
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                appDatabase?.museumDao()?.getFilteredByName(newText)
                    ?.observe(viewLifecycleOwner, Observer { filteredMuseumsList ->
                        museumsList = filteredMuseumsList
                        museumAdapter.notifyDataSetChanged()
                    })
                return true
            }

        })
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
//                bundle.putString("museumName", museumsList[position].name)
//                bundle.putString("museumDescription", museumsList[position].description)
//                bundle.putInt("museumRate", museumsList[position].rate)
//                bundle.putDouble(
//                    "museumLongitude", museumsList[position].location["longitude"] ?: 0.0
//                )
//                bundle.putDouble(
//                    "museumLatitude", museumsList[position].location["latitude"] ?: 0.0
//                )
//                bundle.putString("museumPathToImage", museumsList[position].pathToImage)
                navController.navigate(R.id.action_homeFragment_to_museumDetailsFragment, bundle)
            }

            return rootView.root
        }

    }
}