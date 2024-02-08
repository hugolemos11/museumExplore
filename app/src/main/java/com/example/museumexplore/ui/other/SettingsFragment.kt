package com.example.museumexplore.ui.other

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.R
import com.example.museumexplore.databinding.FragmentSettingsBinding
import com.example.museumexplore.modules.User
import com.example.museumexplore.modules.User.Companion.deleteUserFromFirestore
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class SettingsFragment : Fragment() {

    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private var userId: String? = null
    private var user: User? = null
    private var notificationSwitch: Switch? = null
    private val requestCodeNotificationPermission = 1001
    private var snackBar: Snackbar? = null
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

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

        navController = Navigation.findNavController(view)

        notificationSwitch = binding.notificationSwitch

        val appDatabase = AppDatabase.getInstance(requireContext())
        lifecycleScope.launch {
            if (appDatabase != null) {
                userId?.let { currentUid ->
                    val userData = User.fetchUserData(currentUid)
                    appDatabase.userDao().add(userData)
                    user = appDatabase.userDao().get(currentUid)
                }
            }

            user?.let {currentUser ->
                setImage(currentUser.pathToImage, binding.profileImage, requireContext())
                binding.profileName.text = currentUser.username
            }
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

        binding.removeAccountButton.setOnClickListener {
            val user = Firebase.auth.currentUser
            if (user != null) {
                val builder = AlertDialog.Builder(requireActivity())
                builder.setTitle(getString(R.string.delete_account_confirm))
                builder.setMessage(getString(R.string.delete_account_message))
                builder.setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    showToast("Account Deleted!", requireContext())

                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = user.uid
                                deleteUserFromFirestore(uid, requireContext())
                                Log.d(TAG, "User account deleted.")
                                showToast("Account Deleted!", requireContext())
                                navController.popBackStack()
                            } else {
                                showToast("Error Deleting Account!", requireContext())
                            }
                        }
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                val alert = builder.create()
                alert.show()
            }
        }

        binding.editProfileTextView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("uid", auth.currentUser?.uid)
            bundle.putString("username", user?.username)
            bundle.putString("pathToImage", user?.pathToImage)
            navController.navigate(R.id.action_settingsFragment_to_editProfileFragment, bundle)
        }
    }
    private fun showRevokeSuccessSnackBar() {
        snackBar?.dismiss()
        snackBar = Snackbar.make(
            requireView(),
            "The permission was changed, please reload the application to apply the changes!",
            Snackbar.LENGTH_INDEFINITE,
        ).apply {
            setAction("Exit from MuseumExplore") {
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

    @Suppress("DEPRECATION")
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
                showToast("No permission for Notifications", requireContext())
            }
        }
    }
}