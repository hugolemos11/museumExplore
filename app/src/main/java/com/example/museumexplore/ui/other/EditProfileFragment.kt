package com.example.museumexplore.ui.other

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.databinding.FragmentEditProfileBinding
import com.example.museumexplore.isValidPassword
import com.example.museumexplore.isValidUsername
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
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

        binding.ConfirmButton.setOnClickListener {
            val newPassword = binding.editTextPassword.text.toString()
            val repeatPassword = binding.editTextRepeatPassword.text.toString()
            val username = binding.editTextUsername.text.toString()
            auth = Firebase.auth

            if (newPassword != repeatPassword) {
                binding.textInputLayoutPassword.error = "Passwords Must Match!"
                binding.textInputLayoutRepeatPassword.error = "Passwords Must Match!"
            } else if (isValidPassword(newPassword) && newPassword != ""){
                binding.textInputLayoutPassword.error = null
                binding.textInputLayoutRepeatPassword.error = null

                user!!.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("Password successfully updated", requireContext())
                            Log.d(TAG, "User password updated.")
                            fragmentManager?.popBackStack()
                        } else {
                            showToast("Error updating password", requireContext())
                            Log.e(TAG, "User password update failed: ${task.exception?.message}")
                        }
                    }
            }
            if (isValidUsername(username)) {
                val userUpdates = mapOf(
                    "username" to username
                )
                db.collection("users")
                    .document(auth.uid!!)
                    .update(userUpdates)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showToast("User Updated Successfully!", requireContext())
                            if (newPassword == "") {
                                fragmentManager?.popBackStack()
                            }
                        } else {
                            showToast("Failed to Update User!", requireContext())
                        }
                    }
            } else {
                binding.textInputLayoutUsername.error = "Invalid Username"
            }
        }
    }
}