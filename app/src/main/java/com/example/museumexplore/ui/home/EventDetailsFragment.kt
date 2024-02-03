package com.example.museumexplore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.museumexplore.databinding.FragmentEventDetailsBinding
import com.example.museumexplore.setImage


class EventDetailsFragment : Fragment() {

    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!

    private var id: String? = null
    private var eventTitle: String? = null
    private var eventDescription: String? = null
    private var eventPathToImage: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //navController = Navigation.findNavController(view);

        arguments?.let { bundle ->
            id = bundle.getString("eventId")
            eventTitle = bundle.getString("eventTitle")
            eventDescription = bundle.getString("eventDescription")
            eventPathToImage = bundle.getString("eventPathToImage")
        }

        setImage(eventPathToImage, binding.imageViewEventDetails, requireContext())

        binding.apply {
            textViewEventTitleDetails.text = eventTitle
            textViewEventDescriptionDetails.text = eventDescription
        }
    }
}