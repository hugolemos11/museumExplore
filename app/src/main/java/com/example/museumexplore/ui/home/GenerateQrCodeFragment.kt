package com.example.museumexplore.ui.home
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.databinding.FragmentGenerateQrCodeBinding
import com.example.museumexplore.showToast
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.output.ByteArrayOutputStream
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

class GenerateQrCodeFragment : Fragment() {

    private var _binding: FragmentGenerateQrCodeBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""
        _binding = FragmentGenerateQrCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        binding.buttonGenerate.setOnClickListener{
            val multiFormatWriter = MultiFormatWriter()

            try {
                val bitMatrix: BitMatrix = multiFormatWriter.encode(binding.textEditGenerate.text.toString(), BarcodeFormat.QR_CODE, 200, 200)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)

                addQrCodeToStorage(bitmap)
            } catch (error: WriterException) {
                Log.e("QrCode", error.toString())
            }
        }
    }

    private fun addQrCodeToStorage(bitmap: Bitmap){

        val storage = Firebase.storage
        val storageRef = storage.reference

        // Convert Bitmap to byte array
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        // Create a unique filename (you may adjust this logic according to your requirements)
        val filename = "qrCode_${System.currentTimeMillis()}.png"

        // Upload the byte array to Firebase Storage
        val photoRef = storageRef.child("qrCodes/$filename")
        val uploadTask = photoRef.putBytes(byteArray)

        // Monitor the upload task
        uploadTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // The upload is successful
                showToast("QR Code uploaded successfully", requireContext())
            } else {
                // Handle the error
                showToast("Error uploading QR Code: ${task.exception?.message}", requireContext())
            }
        }
    }
}