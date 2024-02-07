package com.example.museumexplore.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentHomeBinding
import com.example.museumexplore.databinding.MuseumDisplayBinding
import com.example.museumexplore.modules.Museum
import com.example.museumexplore.setImage
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private var museumsList = arrayListOf<Museum>()

    private val museumAdapter = MuseumAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

        navController = Navigation.findNavController(view)

        binding.gridViewMuseums.adapter = museumAdapter

        val appDatabase = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            if (appDatabase != null) {
                val museumsData = Museum.fetchMuseumsData()
                if (museumsData.isNotEmpty()){
                    appDatabase.museumDao().delete()
                    for (museumData in museumsData) {
                        appDatabase.museumDao().add(museumData)
                    }
                }

                appDatabase.museumDao().getAll().observe(viewLifecycleOwner) {
                    museumsList = it as ArrayList<Museum>
                    museumAdapter.notifyDataSetChanged()
                }
            }

            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    appDatabase?.museumDao()?.getFilteredByName(newText)
                        ?.observe(viewLifecycleOwner) { filteredMuseumsList ->
                            museumsList = filteredMuseumsList as ArrayList<Museum>
                            museumAdapter.notifyDataSetChanged()
                        }
                    return true
                }

            })
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

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rootView = MuseumDisplayBinding.inflate(layoutInflater)

            rootView.textViewMuseumName.text = museumsList[position].name

            setImage(museumsList[position].pathToImage, rootView.imageView3, requireContext())

            rootView.root.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("museumId", museumsList[position].id)
                navController.navigate(R.id.action_homeFragment_to_museumDetailsFragment, bundle)
            }

            return rootView.root
        }

    }
}