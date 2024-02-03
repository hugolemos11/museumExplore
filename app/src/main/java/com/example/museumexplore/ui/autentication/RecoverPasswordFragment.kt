package com.example.museumexplore.ui.autentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.databinding.FragmentRecoverPasswordBinding
import com.example.museumexplore.isValidEmail
import com.example.museumexplore.setErrorAndFocus
import com.example.museumexplore.showToast
import com.google.firebase.auth.FirebaseAuth

class RecoverPasswordFragment : Fragment() {

    private var _binding: FragmentRecoverPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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

        // Initialize 'auth' here
        auth = FirebaseAuth.getInstance()

        navController = Navigation.findNavController(view)

        /*.editTextEmailAddress.doOnTextChanged { text, start, before, count ->
            when {
                text.toString().trim().isEmpty() -> {
                    binding.textInputLayoutEmailAddress.error = "Required!"
                }

                !isValidEmail(text.toString().trim()) -> {
                    binding.textInputLayoutEmailAddress.error = "Invalid E-mail!"
                }

                else -> {
                    binding.textInputLayoutEmailAddress.error = null
                }
            }
        }*/
        

        binding.recoverPasswordButton.setOnClickListener {
            val email = binding.editTextEmailAddress.text.toString().trim()
            if (email.isEmpty()) {
                setErrorAndFocus(binding.textInputLayoutEmailAddress, "Required!")
            } else if (!isValidEmail(email)) {
                binding.textInputLayoutEmailAddress.error = "Invalid email!"
            } else {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Email Sent Successfully", requireContext())
                        navController.popBackStack()
                    } else {
                        showToast("Could Not Send Email", requireContext())
                    }
                }
            }
        }
        binding.imageViewBackArrow.setOnClickListener {
            navController.popBackStack()
        }
    }
}