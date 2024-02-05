package com.example.museumexplore.ui.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentPaymentBinding
import com.example.museumexplore.modules.Ticket
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class PaymentFragment : Fragment() {
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth

    private var museumId: String? = null
    private var museumName: String? = null
    private var museumPathToImage: String? = null
    private var ticketTypeId: String? = null
    private var ticketType: String? = null
    private var ticketsAmount: Int? = null
    private var visitDate: String? = null
    private var finalPrice: Double? = null

    private var fileName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
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
            ticketsAmount = bundle.getInt("ticketsAmount")
            visitDate = bundle.getString("visitDate")
            finalPrice = bundle.getDouble("finalPrice")
        }

        navController = Navigation.findNavController(view)

        auth = Firebase.auth

        setImage(museumPathToImage, binding.imageView19, requireContext())

        binding.apply {
            textViewFinalPrice.text = finalPrice.toString() + "€"

            textViewVisitDate.text = "Date for Visit: $visitDate"

            button4.setOnClickListener {
                if (radioButtonMbWay.isChecked || radioButtonMultiBanco.isChecked) {
                    val currentDateTime = getCurrentDateTime()

                    val multiFormatWriter = MultiFormatWriter()

                    val bitMatrix: BitMatrix =
                        multiFormatWriter.encode(
                            "$ticketsAmount for museum $museumName. Type: $ticketType",
                            BarcodeFormat.QR_CODE,
                            200,
                            200
                        )
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)

                    lifecycleScope.launch {
                        val uploaded = addQrCodeToStorage(bitmap)

                        if (uploaded) {
                            Ticket.addTicket(
                                Ticket(
                                    "",
                                    auth.uid!!,
                                    ticketTypeId!!,
                                    museumId!!,
                                    ticketsAmount!!,
                                    convertStringToDate(currentDateTime),
                                    convertStringToDate(visitDate!!),
                                    fileName!!
                                )
                            ) { success ->
                                if (success) {
                                    navController.navigate(R.id.action_paymentFragment_to_finishPaymentFragment)
                                } else {
                                    showToast("Something went wrong buying the ticket!", requireContext())
                                }
                            }
                        } else {
                            showToast(
                                "Something went wrong buying the ticket!",
                                requireContext()
                            )
                        }
                    }
                } else {
                    showToast("Choose the payment method!", requireContext())
                }
            }
        }
    }

    private fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val currentDate = dateFormat.format(calendar.time)
        val currentTime = timeFormat.format(calendar.time)

        return "$currentDate $currentTime"
    }

    private fun convertStringToDate(dateString: String): Date {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.parse(dateString) ?: Date()
    }

    private suspend fun addQrCodeToStorage(bitmap: Bitmap): Boolean {
        return suspendCoroutine { continuation ->
            val storage = Firebase.storage
            val storageRef = storage.reference

            // Convert Bitmap to byte array
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray = stream.toByteArray()

            // Create a unique filename (you may adjust this logic according to your requirements)
            fileName = "qrCodes/qrCode_${System.currentTimeMillis()}.png"

            // Upload the byte array to Firebase Storage
            val photoRef = storageRef.child("$fileName")
            val uploadTask = photoRef.putBytes(byteArray)

            // Monitor the upload task
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            }
        }
    }
}