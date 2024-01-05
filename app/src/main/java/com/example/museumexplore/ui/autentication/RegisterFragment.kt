package com.example.museumexplore.ui.autentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.databinding.FragmentRegisterBinding
import com.example.museumexplore.modules.User
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

        binding.registerButton.setOnClickListener {
            val email = binding.editTextEmailAddress.text.toString()
            val username = binding.editTextUsername.text.toString()
            val password = binding.editTextPassword.text.toString()

            /*auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        navController.navigate(R.id.action_global_homeNavigation)
                    } else {
                        Toast.makeText(
                            context,
                            "Erro no registo",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }*/

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) {
                    if (it.isSuccessful) {
                        var user = User(username, null, password, "userImages/default_user.png")

                        db.collection("users")
                            .document(auth.uid!!)
                            .set(user).addOnCompleteListener{task ->
                                if (task.isSuccessful) {
                                    showToast("User Registered Successfully!", requireContext())
                                }
                                else {
                                    showToast("User Failed to Registered", requireContext())
                                }
                            }
                    }
                }
        }
    }
}