package com.example.museumexplore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.databinding.FragmentEventDetailsBinding
import com.example.museumexplore.modules.ArtWork
import com.example.museumexplore.modules.Category
import com.example.museumexplore.modules.Event
import com.example.museumexplore.setImage
import kotlinx.coroutines.launch


class EventDetailsFragment : Fragment() {

    private var _binding: FragmentEventDetailsBinding? = null
    private val binding get() = _binding!!

    private var id: String? = null

    private var event: Event? = null

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
        }

        val appDatabase = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            if (appDatabase != null) {
                id?.let { currentEventId ->
                    val eventData = Event.fetchEventData(currentEventId)
                    appDatabase.eventDao().add(eventData)
                    event = appDatabase.eventDao().get(currentEventId)
                }
            }

            event?.let { currentEvent ->
                setImage(currentEvent.pathToImage, binding.imageViewEventDetails, requireContext())

                binding.apply {
                    textViewEventTitleDetails.text = currentEvent.title
                    textViewEventDescriptionDetails.text = currentEvent.description
                }

            }
        }
    }

}