package com.example.museumexplore.ui.other

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.databinding.FragmentEditProfileBinding
import com.example.museumexplore.isValidEmail
import com.example.museumexplore.isValidPassword
import com.example.museumexplore.isValidUsername
import com.example.museumexplore.modules.User
import com.example.museumexplore.setErrorAndFocus
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.RuntimeException

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private var userId: String? = null
    private var username: String? = null
    private var pathToImage: String? = null
    private val user = Firebase.auth.currentUser
    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private var usernamesInUse: ArrayList<String> = ArrayList()
    private var formIsValid: Boolean? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            userId = bundle.getString("uid")
            username = bundle.getString("username")
            pathToImage = bundle.getString("pathToImage")
        }

        navController = Navigation.findNavController(view)

        setImage(pathToImage, binding.imageViewUser, requireContext())
        binding.editTextUsername.text = Editable.Factory.getInstance().newEditable(username ?: "")

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
                // Checks that the username entered is not already in use, only if the username is not that of the user
                if (username != binding.editTextUsername.text.toString().trim()){
                    for (username in usernamesInUse) {
                        if (username == binding.editTextUsername.text.toString().trim()) {
                            binding.textInputLayoutUsername.error = "The Username is Already in use!"
                        }
                    }
                }
            }
        }

        binding.editTextOldPassword.doOnTextChanged { text, _, _, _ ->
            when {
                !isValidPassword(text.toString().trim()) -> {
                    binding.textInputLayoutOldPassword.error = "Invalid Password!"
                }

                else -> {
                    binding.textInputLayoutOldPassword.error = null
                }
            }
        }

        binding.editTextPassword.doOnTextChanged { text, _, _, _ ->
            when {
                !isValidPassword(text.toString().trim()) -> {
                    binding.textInputLayoutPassword.error = "Invalid Password!"
                }

                else -> {
                    binding.textInputLayoutPassword.error = null
                }
            }
        }

        binding.editTextRepeatPassword.doOnTextChanged { text, _, _, _ ->
            when {
                !isValidPassword(text.toString().trim()) -> {
                    binding.textInputLayoutRepeatPassword.error = "Invalid Password!"
                }

                else -> {
                    binding.textInputLayoutRepeatPassword.error = null
                }
            }
        }

        binding.ConfirmButton.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val oldPassword = binding.editTextOldPassword.text.toString().trim()
            val newPassword = binding.editTextPassword.text.toString().trim()

            if ((username.isNotEmpty() && oldPassword.isEmpty()) || (username.isNotEmpty() && oldPassword.isNotEmpty() && newPassword.isEmpty())) {
                validateAndUpdateUsername()
            } else if (username.isNotEmpty() && oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                validateAndUpdatePassword()
            }
        }

        fetchUsernamesData()
    }

    private fun validateAndUpdateUsername() {
        val username = binding.editTextUsername.text.toString().trim()

        formIsValid = if (username.isEmpty() || !isValidUsername(username)) {
            binding.textInputLayoutUsername.requestFocus()
            false
        } else {
            true
        }

        if (formIsValid == true) {
            lifecycleScope.launch {
                if (username != this@EditProfileFragment.username) {
                    updateUsername(username) { success ->
                        if (success) {
                            showToast("User Updated Successfully!", requireContext())
                            fragmentManager?.popBackStack()
                        } else {
                            showToast("Failed to Update User!", requireContext())
                        }
                    }
                }
                else {
                    showToast("Change some data!", requireContext())
                }
            }
        }
    }

    private fun validateAndUpdatePassword() {
        auth = Firebase.auth
        val username = binding.editTextUsername.text.toString().trim()
        val oldPassword = binding.editTextOldPassword.text.toString().trim()
        val newPassword = binding.editTextPassword.text.toString().trim()
        val repeatPassword = binding.editTextRepeatPassword.text.toString().trim()

        formIsValid = if (oldPassword.isEmpty()) {
            setErrorAndFocus(binding.textInputLayoutOldPassword, "Required!")
            false
        } else {
            true
        }

        formIsValid = if (!isValidPassword(oldPassword)) {
            binding.textInputLayoutOldPassword.requestFocus()
            false
        } else {
            true
        }

        formIsValid = if (newPassword.isEmpty()) {
            setErrorAndFocus(binding.textInputLayoutPassword, "Required!")
            false
        } else {
            true
        }

        formIsValid = if (!isValidPassword(newPassword)) {
            binding.textInputLayoutPassword.requestFocus()
            false
        } else {
            true
        }

        formIsValid = if (repeatPassword.isEmpty()) {
            setErrorAndFocus(binding.textInputLayoutRepeatPassword, "Required!")
            false
        } else {
            true
        }

        formIsValid = if (!isValidPassword(repeatPassword)) {
            binding.textInputLayoutRepeatPassword.requestFocus()
            false
        } else {
            true
        }

        formIsValid = if (oldPassword == newPassword) {
            setErrorAndFocus(binding.textInputLayoutOldPassword, "Passwords are the same!")
            setErrorAndFocus(binding.textInputLayoutPassword, "Passwords are the same!")
            false
        } else {
            true
        }

        formIsValid = if (newPassword != repeatPassword) {
            setErrorAndFocus(binding.textInputLayoutPassword, "Passwords must match!")
            setErrorAndFocus(binding.textInputLayoutRepeatPassword, "Passwords must match!")
            false
        } else {
            true
        }

        if (formIsValid == true) {
            user?.let { currentUser ->
                val credential =
                    EmailAuthProvider.getCredential(currentUser.email!!, oldPassword)

                currentUser.reauthenticate(credential)
                    .addOnCompleteListener { reAuthTask ->
                        if (reAuthTask.isSuccessful) {
                            // User has been successfully reauthenticated
                            // Now you can update the password
                            lifecycleScope.launch {
                                if (username != this@EditProfileFragment.username) {
                                    updateUsername(username) { success ->
                                        if (success) {
                                            currentUser.updatePassword(newPassword)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        showToast(
                                                            "User data successfully updated!",
                                                            requireContext()
                                                        )
                                                        fragmentManager?.popBackStack()
                                                    } else {
                                                        showToast(
                                                            "Error updating password!",
                                                            requireContext()
                                                        )
                                                        Log.e(
                                                            TAG,
                                                            "User password update failed: ${task.exception?.message}"
                                                        )
                                                    }
                                                }
                                        } else {
                                            showToast(
                                                "Failed to Update User!",
                                                requireContext()
                                            )
                                        }
                                    }

                                } else {
                                    currentUser.updatePassword(newPassword)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                showToast(
                                                    "Password successfully updated!",
                                                    requireContext()
                                                )
                                                fragmentManager?.popBackStack()
                                            } else {
                                                showToast(
                                                    "Error updating password!",
                                                    requireContext()
                                                )
                                                Log.e(
                                                    TAG,
                                                    "User password update failed: ${task.exception?.message}"
                                                )
                                            }
                                        }
                                }
                            }
                        } else {
                            showToast("Reauthentication failed", requireContext())
                            setErrorAndFocus(
                                binding.textInputLayoutOldPassword,
                                "Incorrect password!"
                            )
                        }
                    }
            }
        }
    }

    private suspend fun updateUsername(username: String, callback: (Boolean) -> Unit) {
        val userUpdates = mapOf("username" to username)
        val appDatabase = AppDatabase.getInstance(requireContext())

        if (appDatabase != null) {
            userId?.let { currentUserId ->
                try {
                    val updatedUserData =
                        User.updateUserData(currentUserId, userUpdates).await()
                    appDatabase.userDao().add(updatedUserData)
                    callback(true)
                } catch (e: RuntimeException) {
                    callback(false)
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