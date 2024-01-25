package com.example.museumexplore.ui.other

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.abs
import kotlin.system.exitProcess

class SettingsFragment : Fragment() {

    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private val db = Firebase.firestore
    private var userId: String? = null
    private var user: User? = null
    private var notificationSwitch: Switch? = null
    private val requestCodeNotificationPermission = 1001
    private var snackBar: Snackbar? = null

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { bundle ->
            userId = bundle.getString("uid")
        }
        // Remove the title of fragment on the actionBar
        (activity as AppCompatActivity).supportActionBar?.title = ""

        navController = Navigation.findNavController(view)
        notificationSwitch = binding.notificationSwitch
        if (userId != null) {
            fetchUserData(userId!!)
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) {
            notificationSwitch?.isChecked = true
        }

        notificationSwitch!!.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                askForNotificationPermission()
            } else {
                requireContext().revokeSelfPermissionOnKill(android.Manifest.permission.POST_NOTIFICATIONS)
                showRevokeSuccessSnackBar()
            }
        }
        if (notificationSwitch!!.isChecked) {
            askForNotificationPermission()
        }

    }
    private fun showRevokeSuccessSnackBar() {
        snackBar?.dismiss()
        snackBar = Snackbar.make(
            requireView(),
            "A permissão foi removida, por favor, reinicie a aplicação para aplicar as alterações.",
            Snackbar.LENGTH_INDEFINITE,
        ).apply {
            setAction("Sair da aplicação") {
                exitProcess(0)
            }
        }
        snackBar?.show()
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askForNotificationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            requestCodeNotificationPermission
        )
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeNotificationPermission && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                notificationSwitch?.isChecked = true
            } else {
                showToast("Não deu permissão para as notificações.", requireContext())
            }
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