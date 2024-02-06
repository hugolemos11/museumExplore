package com.example.museumexplore.ui.autentication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentRegisterBinding
import com.example.museumexplore.isValidEmail
import com.example.museumexplore.isValidPassword
import com.example.museumexplore.isValidUsername
import com.example.museumexplore.modules.User
import com.example.museumexplore.setErrorAndFocus
import com.example.museumexplore.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.RuntimeException

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var registerIsValid: Boolean? = null
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

        binding.editTextUsername.doOnTextChanged { text, _, _, _ ->
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

    @SuppressLint("SetTextI18n")
    private fun validateAndRegisterUser() {

        val email = binding.editTextEmailAddress.text.toString().trim()
        val username = binding.editTextUsername.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        registerIsValid = true

        // Validate email
        if (email.isEmpty()) {
            setErrorAndFocus(binding.textInputLayoutEmailAddress, "Required!")
            registerIsValid = false
        } else if (!isValidEmail(email)) {
            binding.textInputLayoutEmailAddress.requestFocus()
            setErrorAndFocus(binding.textInputLayoutEmailAddress, "Invalid Email!")
            registerIsValid = false
        }

        // Validate username
        if (username.isEmpty()) {
            setErrorAndFocus(binding.textInputLayoutUsername, "Required!")
            registerIsValid = false
        } else if (!isValidUsername(username) || usernamesInUse.contains(username)) {
            setErrorAndFocus(binding.textInputLayoutUsername, "Invalid or Already in Use!")
            registerIsValid = false
        }

        // Validate password
        if (password.isEmpty()) {
            setErrorAndFocus(binding.textInputLayoutPassword, "Required!")
            registerIsValid = false
        } else if (!isValidPassword(password)) {
            setErrorAndFocus(binding.textInputLayoutPassword, "Invalid Password!")
            registerIsValid = false
        }

        // Validate checkbox
        if (!binding.checkBox.isChecked) {
            binding.errorTextView.visibility = View.VISIBLE
            binding.errorTextView.text = "Required!"
            registerIsValid = false
        }

        if (registerIsValid == true) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) {
                    if (it.isSuccessful) {
                        val appDatabase = AppDatabase.getInstance(requireContext())
                        if (appDatabase != null) {
                            auth.uid?.let { currentUid ->
                                lifecycleScope.launch {
                                    try {
                                        val updatedUserData =
                                            User.createUser(currentUid, username).await()
                                        appDatabase.userDao().add(updatedUserData)
                                        showToast(
                                            "User Registered Successfully!",
                                            requireContext()
                                        )
                                        navController.navigate(R.id.action_autenticationNavigation_to_homeNavigation)
                                    } catch (e: RuntimeException) {
                                        showToast(
                                            "User Failed to Registered!",
                                            requireContext()
                                        )
                                    }
                                }
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