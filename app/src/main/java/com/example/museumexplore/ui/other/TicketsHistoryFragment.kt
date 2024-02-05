package com.example.museumexplore.ui.other

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.databinding.FragmentTicketsHistoryBinding
import com.example.museumexplore.databinding.TicketDisplayBinding
import com.example.museumexplore.modules.Museum
import com.example.museumexplore.modules.Ticket
import com.example.museumexplore.setImage
import com.example.museumexplore.ui.dialog.TicketDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TicketsHistoryFragment : Fragment() {

    private var _binding: FragmentTicketsHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private var uid: String? = null

    private var ticketsList = arrayListOf<Ticket>()

    private val ticketAdapter = TicketAdapter()

    private var museum: Museum? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketsHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            uid = bundle.getString("uid")
        }

        navController = Navigation.findNavController(view)

        binding.listView.adapter = ticketAdapter

        val appDatabase = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            if (appDatabase != null) {
                uid?.let { currentUid ->
                    val museumsData = Museum.fetchMuseumsData()
                    for (museumData in museumsData) {
                        appDatabase.museumDao().add(museumData)
                    }
                    val ticketsData = Ticket.fetchTicketsData(currentUid)
                    for (ticketData in ticketsData) {
                        appDatabase.ticketDao().add(ticketData)
                    }
                    appDatabase.ticketDao().getAll(currentUid).observe(viewLifecycleOwner) {
                        ticketsList = it as ArrayList<Ticket>
                        ticketAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    inner class TicketAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return ticketsList.size
        }

        override fun getItem(position: Int): Any {
            return ticketsList[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        @SuppressLint("ViewHolder")
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rootView = TicketDisplayBinding.inflate(layoutInflater)

            val appDatabase = AppDatabase.getInstance(requireContext())
            lifecycleScope.launch {
                if (appDatabase != null) {
                    uid?.let {
                        museum = appDatabase.museumDao().get(ticketsList[position].museumId)
                    }
                }
            }

            setImage(museum?.pathToImage, rootView.imageViewMuseumImage, requireContext())

            rootView.textViewMuseumName.text = museum?.name
            val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy hh:MM", Locale.getDefault())
            val formattedDateTime = dateTimeFormat.format(ticketsList[position].visitDate.time)
            rootView.textViewVisitDate.text = formattedDateTime.toString()

            rootView.root.setOnClickListener {
                val fragmentManager = requireActivity().supportFragmentManager
                val bundle = Bundle()
                bundle.putString("ticketId", ticketsList[position].id)
                bundle.putString("museumName", museum?.name)
                bundle.putString("visitDate", formattedDateTime.toString())
                bundle.putString("pathToImage", ticketsList[position].pathToImage)
                TicketDialog.show(fragmentManager, bundle)
            }
            return rootView.root
        }
    }
}