package com.example.museumexplore.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentHomeBinding
import com.example.museumexplore.databinding.FragmentProfileBinding
import com.example.museumexplore.databinding.MuseumDisplayBinding
import com.example.museumexplore.modules.Museum
import com.example.museumexplore.ui.home.MuseumDetailsFragment

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}