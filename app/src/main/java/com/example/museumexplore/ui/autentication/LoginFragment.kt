package com.example.museumexplore.ui.autentication

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentLoginBinding
import com.example.museumexplore.isValidEmail
import com.example.museumexplore.isValidPassword
import com.example.museumexplore.setErrorAndFocus
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
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

        binding.editTextEmailAddress.doOnTextChanged { text, _, _, _ ->
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
        }

        binding.editTextPassword.doOnTextChanged { text, _, _, _ ->
            when {
                text.toString().trim().isEmpty() -> {
                    binding.textInputLayoutPassword.error = "Required!"
                }

                !isValidPassword(text.toString().trim()) -> {
                    binding.textInputLayoutPassword.error = "Invalid Password!"
                }

                else -> {
                    binding.textInputLayoutPassword.error = null
                }
            }
        }

        binding.textViewRegister.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.textViewPassword.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_recoverPasswordFragment)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.editTextEmailAddress.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()

            when {
                email.isEmpty() -> setErrorAndFocus(binding.textInputLayoutEmailAddress, "Required!")
                !isValidEmail(email) -> binding.textInputLayoutEmailAddress.requestFocus()

                password.isEmpty() -> setErrorAndFocus(binding.textInputLayoutPassword, "Required!")
                !isValidPassword(password) -> binding.textInputLayoutPassword.requestFocus()

                else -> {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "signInWithEmail:success")

                                val navOptions = NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build()

                                val currentDestinationId = navController.currentDestination?.id

                                when (currentDestinationId) {
                                    R.id.registerFragment, R.id.recoverPasswordFragment -> {
                                        navController.navigate(
                                            R.id.action_autenticationNavigation_to_homeNavigation,
                                            null,
                                            navOptions
                                        )
                                    }
                                    else -> {
                                        // Pop the back stack
                                        navController.popBackStack()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Authentication failed.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                }
            }


        }

        binding.imageViewBackArrow.setOnClickListener {
            navController.popBackStack()
        }
    }
}