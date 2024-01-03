package com.example.museumexplore.ui.autentication

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.museumexplore.databinding.FragmentRecoverPasswordBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverPasswordFragment : Fragment() {

    private var _binding: FragmentRecoverPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recoverPasswordButton.setOnClickListener {
            activity?.fragmentManager?.popBackStack()
        }

        binding.editTextEmailAddress.setOnClickListener {
            val email = binding.editTextEmailAddress.text.toString()
            Firebase.auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Email Sent Successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.d(TAG, "Email sent.")
                    } else {
                        Toast.makeText(requireContext(), "Could Not Send Email", Toast.LENGTH_LONG)
                            .show()
                    }
                }
        }
    }
}