package com.example.museumexplore.ui.autentication

import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentRegisterBinding
import com.example.museumexplore.isValidEmail
import com.example.museumexplore.isValidPassword
import com.example.museumexplore.isValidUsername
import com.example.museumexplore.modules.Event
import com.example.museumexplore.modules.EventAdapter
import com.example.museumexplore.modules.User
import com.example.museumexplore.setErrorAndFocus
import com.example.museumexplore.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    private var usernamesInUse: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize 'auth' here
        auth = Firebase.auth

        navController = Navigation.findNavController(view)

        binding.editTextEmailAddress.doOnTextChanged { text, start, before, count ->
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

        binding.editTextUsername.doOnTextChanged { text, start, before, count ->
            when {
                text.toString().trim().isEmpty() -> {
                    binding.textInputLayoutUsername.error = "Required!"
                }

                !isValidUsername(text.toString().trim()) -> {
                    binding.textInputLayoutUsername.error = "Invalid Username!"
                }

                else -> {
                    binding.textInputLayoutUsername.error = null
                }
            }
        }

        binding.editTextUsername.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                // Verify if the username inserted is not already in use
                for (username in usernamesInUse) {
                    if (username == binding.editTextUsername.text.toString().trim()) {
                        binding.textInputLayoutUsername.error = "The Username is Already in use!"
                    }
                }
            }
        }

        binding.editTextPassword.doOnTextChanged { text, start, before, count ->
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

        binding.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.errorTextView.visibility = View.GONE
            }
        }

        binding.registerButton.setOnClickListener {
            validateAndRegisterUser()
        }

        binding.imageViewBackArrow.setOnClickListener {
            navController.popBackStack()
        }

        fetchUsernamesData()
    }

    private fun validateAndRegisterUser() {
        val email = binding.editTextEmailAddress.text.toString().trim()
        val username = binding.editTextUsername.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()
        when {
            email.isEmpty() -> setErrorAndFocus(binding.textInputLayoutEmailAddress, "Required!")
            !isValidEmail(email) -> binding.textInputLayoutEmailAddress.requestFocus()

            username.isEmpty() -> setErrorAndFocus(binding.textInputLayoutUsername, "Required!")
            !isValidUsername(username) -> binding.textInputLayoutUsername.requestFocus()

            usernamesInUse.contains(username) -> binding.textInputLayoutUsername.requestFocus()

            password.isEmpty() -> setErrorAndFocus(binding.textInputLayoutPassword, "Required!")
            !isValidUsername(password) -> binding.textInputLayoutPassword.requestFocus()

            !binding.checkBox.isChecked -> {
                binding.errorTextView.visibility = View.VISIBLE
                binding.errorTextView.text = "Required!"
            }

            else -> {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) {
                        if (it.isSuccessful) {
                            val user = User(username, null, "userImages/default_user.png")

                            db.collection("users")
                                .document(auth.uid!!)
                                .set(user).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        showToast("User Registered Successfully!", requireContext())
                                        navController.navigate(R.id.action_global_homeNavigation)
                                    } else {
                                        showToast("User Failed to Registered!", requireContext())
                                    }
                                }
                        }
                    }
                    .addOnFailureListener {
                        setErrorAndFocus(
                            binding.textInputLayoutEmailAddress,
                            "The Email is Already in Use!"
                        )
                    }
            }
        }
    }

    private fun fetchUsernamesData() {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val username = document.getString("username")

                    if (username != null) {
                        this.usernamesInUse.add(username)
                    }
                }
            }
            .addOnFailureListener {
                showToast("An error occurred: ${it.localizedMessage}", requireContext())
            }
    }
}