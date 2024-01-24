package com.example.museumexplore.ui.other

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.museumexplore.databinding.FragmentSettingsBinding
import com.example.museumexplore.databinding.FragmentTicketBinding
import com.example.museumexplore.modules.Ticket
import com.example.museumexplore.modules.TicketAdapter
import com.example.museumexplore.modules.User
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.abs

class SettingsFragment : Fragment() {

    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val db = Firebase.firestore
    private var userId: String? = null
    private var user: User? = null
    private lateinit var headerImage: ImageView
    private lateinit var headerUserName: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
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
        }
        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""

        navController = Navigation.findNavController(view)

        if (userId != null) {
            fetchUserData(userId!!)
        }
    }

    private fun fetchUserData(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                it.data?.let { data ->
                    user = User.fromSnapshot(data)
                    setImage(user?.pathToImage, binding.profileImage, requireContext())
                    binding.profileName.text = user?.username
                }
            }
            .addOnFailureListener {
                showToast("An error occurred: ${it.localizedMessage}", requireContext())
            }
    }
}