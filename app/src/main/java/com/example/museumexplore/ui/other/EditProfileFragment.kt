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
import com.example.museumexplore.modules.User
import com.example.museumexplore.setImage
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
    private val db = Firebase.firestore
    private var password : String? = null
    val user = Firebase.auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""

        navController = Navigation.findNavController(view)

        setImage(pathToImage, binding.imageViewUser, requireContext())

        binding.editTextUsername.text = Editable.Factory.getInstance().newEditable(username ?: "")

        binding.ConfirmButton.setOnClickListener {
            if (binding.editTextPassword != binding.editTextRepeatPassword) {
                binding.textInputLayoutPassword.error = "Passwords Must Match!"
                binding.textInputLayoutRepeatPassword.error = "Passwords Must Match!"
            } else {
                password = binding.editTextPassword.toString()
            }
            user!!.updatePassword(password!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User password updated.")
                    }
                }
                }
    }
}