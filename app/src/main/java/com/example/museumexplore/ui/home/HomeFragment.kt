package com.example.museumexplore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentHomeBinding
import com.example.museumexplore.databinding.MuseumDisplayBinding
import com.example.museumexplore.modules.Museum


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

        navController = Navigation.findNavController(view);

        museums.add(Museum("aa", "Museumxzy", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
        museums.add(Museum("ab", "Museumxyz", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."))
        museums.add(Museum("ac", "Museumxzy1", ""))
        museums.add(Museum("ba", "Museumxyz1", ""))
        museums.add(Museum("bb", "Museumxzy2", ""))
        museums.add(Museum("bc", "Museumxyz2", ""))
        museums.add(Museum("ca", "Museumxzy3", ""))
        museums.add(Museum("cb", "Museumxyz3", ""))

        binding.gridViewMuseums.adapter = adapter
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

            rootView.root.setOnClickListener{
                /*val bundle = Bundle()
                bundle.putString(MuseumDetailsFragment.EXTRA_NAME, museums[position].name)
                bundle.putString(MuseumDetailsFragment.EXTRA_DESCRIPTION, museums[position].description)
                findNavController().navigate(R.id.action_navigation_home_to_museumDetailsFragment, bundle)*/

                navController.navigate(R.id.action_homeFragment_to_museumDetailsFragment)
            }

            return rootView.root
        }

    }
}