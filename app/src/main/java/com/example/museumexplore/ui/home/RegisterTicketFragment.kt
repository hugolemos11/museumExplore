package com.example.museumexplore.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentRegisterTicketBinding
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegisterTicketFragment : Fragment() {
    private var _binding: FragmentRegisterTicketBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val calendar = Calendar.getInstance()

    private var dateData: String? = null
    private var timeData: String? = null

    private var museumId: String? = null
    private var museumName: String? = null
    private var museumPathToImage: String? = null
    private var ticketTypeId: String? = null
    private var ticketType: String? = null
    private var ticketTypePrice: Double? = null
    private var ticketTypeMaxToBuy: Int? = null

    private var ticketsAmount: Int
        get() = binding.textViewAmount.text.toString().toInt()
        set(value) {
            ticketTypeMaxToBuy?.let { currentTicketTypeMaxToBuy ->
                if (value in 1..currentTicketTypeMaxToBuy) {
                    binding.textViewAmount.text = value.toString()
                    finalPrice = ticketTypePrice!! * value
                }
            }
        }

    private var finalPrice: Double
        get() =
            binding.textViewPrice.text.toString().replace("[^\\d.]".toRegex(), "").toDouble()
        @SuppressLint("SetTextI18n")
        set(value) {
            binding.textViewPrice.text = DecimalFormat("#.##").format(value) + " €"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterTicketBinding.inflate(inflater, container, false)
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
            museumName = bundle.getString("museumName")
            museumPathToImage = bundle.getString("museumPathToImage")
            ticketTypeId = bundle.getString("ticketTypeId")
            ticketType = bundle.getString("ticketType")
            ticketTypePrice = bundle.getDouble("ticketPrice")
            ticketTypeMaxToBuy = bundle.getInt("ticketMaxToBuy")
        }

        navController = Navigation.findNavController(view)

        setImage(museumPathToImage, binding.imageView6, requireContext())

        binding.apply {
            textViewMuseumNameRegisterTicket.text = museumName

            textViewPrice.text = "$ticketTypePrice €"

            if (dateData != null && timeData != null) {
                textViewSelecteDate.text = dateData

                textViewSelectedTime.text = timeData
            }

            imageViewCalendar.setOnClickListener {
                showDatePicker()
            }

            imageViewSchedule.setOnClickListener {
                showTimePicker()
            }

            buttonIncrement.setOnClickListener {
                ticketsAmount++
            }
            buttonDecrement.setOnClickListener {
                ticketsAmount--
            }

            textViewAmount.text = ticketsAmount.toString()

            textViewPrice.text = finalPrice.toString()

            buttonBuy.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("museumId", museumId)
                bundle.putString("museumName", museumName)
                bundle.putString("museumPathToImage", museumPathToImage)
                bundle.putString("ticketTypeId", ticketTypeId)
                bundle.putString("ticketType", ticketType)
                bundle.putInt("ticketsAmount", ticketsAmount)
                bundle.putString("visitDate", "$dateData $timeData")
                bundle.putDouble("finalPrice", finalPrice)

                // Check if both date and time are selected
                if (dateData != null && timeData != null) {
                    navController.navigate(
                        R.id.action_registerTicketFragment_to_paymentFragment,
                        bundle
                    )
                } else {
                    showToast("Please select the date and time for the visit!", requireContext())
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                dateData = formattedDate
                binding.textViewDate.text = "Selected Date: $formattedDate"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            requireContext(), { _, hourOfDay: Int, minuteOfDay: Int ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minuteOfDay)
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = timeFormat.format(selectedTime.time)
                timeData = formattedTime
                binding.textViewTime.text = "Selected Time: $formattedTime"
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }
}