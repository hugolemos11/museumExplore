package com.example.museumexplore.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentTicketBinding
import com.example.museumexplore.modules.Museum
import com.example.museumexplore.modules.TicketAdapter
import com.example.museumexplore.modules.TicketType
import kotlinx.coroutines.launch
import kotlin.math.abs

class TicketFragment : Fragment() {

    private var _binding: FragmentTicketBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private var ticketTypesList = ArrayList<TicketType>()

    private lateinit var ticketTypeSelected: TicketType

    private lateinit var imageView: ViewPager2

    private var museumId: String? = null

    private var museum: Museum? = null

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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            museumId = bundle.getString("museumId")
        }

        navController = Navigation.findNavController(view)

        binding.checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.errorTextView.visibility = View.GONE
            }
        }
        binding.button.setOnClickListener {
            if (!binding.checkBoxTerms.isChecked) {
                binding.errorTextView.visibility = View.VISIBLE
                binding.errorTextView.text = "Required!"
            } else {
                val bundle = Bundle()
                bundle.putString("museumId", museumId)
                museum?.let {currentMuseum ->
                    bundle.putString("museumName", currentMuseum.name)
                    bundle.putString("museumPathToImage", currentMuseum.pathToImage)
                }
                bundle.putString("ticketTypeId", ticketTypeSelected.id)
                bundle.putString("ticketType", ticketTypeSelected.type)
                bundle.putDouble("ticketPrice", ticketTypeSelected.price)
                bundle.putInt("ticketMaxToBuy", ticketTypeSelected.maxTobuy)
                navController.navigate(R.id.action_ticketFragment_to_registerTicketFragment, bundle)
            }
        }

        val appDatabase = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            if (appDatabase != null) {
                museumId?.let {currentMuseumId ->
                    museum = appDatabase.museumDao().get(currentMuseumId)
                    val ticketTypesData = TicketType.fetchTicketTypesData(currentMuseumId)
                    if (ticketTypesData.isNotEmpty()) {
                        appDatabase.ticketTypesDao().delete()
                        for (ticketTypeData in ticketTypesData) {
                            appDatabase.ticketTypesDao().add(ticketTypeData)
                        }
                    }
                    appDatabase.ticketTypesDao().getAll(currentMuseumId).observe(viewLifecycleOwner) {
                        ticketTypesList = it as ArrayList<TicketType>
                        binding.viewPager.adapter = TicketAdapter(ticketTypesList, requireContext())
                    }
                }
            }
        }
        transformer()
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

        imageView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                ticketTypeSelected = ticketTypesList[position]
            }
        })
    }
}