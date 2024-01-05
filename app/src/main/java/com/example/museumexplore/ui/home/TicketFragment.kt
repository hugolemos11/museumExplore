package com.example.museumexplore.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.museumexplore.databinding.FragmentTicketBinding
import com.example.museumexplore.modules.Ticket
import com.example.museumexplore.modules.TicketAdapter
import com.example.museumexplore.showToast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.abs

class TicketFragment : Fragment() {

    private var _binding: FragmentTicketBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private var ticketList = ArrayList<Ticket>()

    private lateinit var imageView: ViewPager2

    private val db = Firebase.firestore

    private var museumId: String? = null
    private var museumName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketBinding.inflate(inflater, container, false)
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

        transformer()
        fetchTicketsData()
    }

    private fun transformer() {
        imageView = binding.viewPager
        imageView.clipChildren = false
        imageView.clipToPadding = false
        imageView.offscreenPageLimit = 3
        imageView.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()

        compositePageTransformer.addTransformer(MarginPageTransformer(40))

        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)

            page.scaleY = 0.85f + r * 0.15f
        }
        imageView.setPageTransformer(compositePageTransformer)
    }

    private fun fetchTicketsData() {
        db.collection("ticketTypes")
            .whereEqualTo("museumId", museumId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val ticket = Ticket.fromSnapshot(document.id, document.data)
                    this.ticketList.add(ticket)
                }
                binding.viewPager.adapter = TicketAdapter(ticketList, requireContext())
            }
            .addOnFailureListener {
                showToast("An error occurred: ${it.localizedMessage}", requireContext())
            }
    }
}