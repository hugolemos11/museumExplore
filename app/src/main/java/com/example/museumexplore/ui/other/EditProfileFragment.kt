package com.example.museumexplore.ui.other

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.museumexplore.AppDatabase
import com.example.museumexplore.databinding.FragmentEditProfileBinding
import com.example.museumexplore.isValidPassword
import com.example.museumexplore.isValidUsername
import com.example.museumexplore.modules.User
import com.example.museumexplore.setErrorAndFocus
import com.example.museumexplore.setImage
import com.example.museumexplore.showToast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private var userId: String? = null
    private var usernameGlobal: String? = null
    private var pathToImage: String? = null
    private val user = Firebase.auth.currentUser
    private val db = Firebase.firestore
    private var usernamesInUse: ArrayList<String> = ArrayList()
    private var formIsValid: Boolean? = null
    private val requestCodeCameraPermission = 1001
    private var fileName: String? = null
    private var bitMap: Bitmap? = null

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
            usernameGlobal = bundle.getString("username")
            pathToImage = bundle.getString("pathToImage")
        }

        navController = Navigation.findNavController(view)

        setImage(pathToImage, binding.imageViewUser, requireContext())


        binding.imageViewEditPhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }
        binding.editTextUsername.text =
            Editable.Factory.getInstance().newEditable(usernameGlobal ?: "")

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
                if (usernameGlobal != binding.editTextUsername.text.toString().trim()) {
                    for (username in usernamesInUse) {
                        if (username == binding.editTextUsername.text.toString().trim()) {
                            binding.textInputLayoutUsername.error =
                                "The Username is Already in use!"
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

            if ((bitMap == null && username == usernameGlobal && oldPassword.isEmpty() && newPassword.isEmpty()) || (bitMap == null && username == usernameGlobal && oldPassword.isNotEmpty() && newPassword.isEmpty())) {
                showToast("Change some data!", requireContext())
            } else if ((bitMap != null && username.isNotEmpty() && username == usernameGlobal && oldPassword.isNotEmpty()) || (bitMap != null && username.isNotEmpty() && username == usernameGlobal && oldPassword.isEmpty() && newPassword.isEmpty())) {
                updateImage()
            } else if ((username.isNotEmpty() && oldPassword.isEmpty() && newPassword.isEmpty()) || (username.isNotEmpty() && oldPassword.isNotEmpty() && newPassword.isEmpty())) {
                validateAndUpdateUsername()
            } else if ((username.isNotEmpty() && oldPassword.isNotEmpty() && newPassword.isNotEmpty()) || (username.isNotEmpty() && oldPassword.isEmpty() && newPassword.isNotEmpty())) {
                validateAndUpdatePassword()
            }
        }

        fetchUsernamesData()
    }

    private fun updateImage() {
        lifecycleScope.launch {
            val imageUploaded = bitMap?.let { addImageToStorage(it) }
            if (imageUploaded == true) {
                updateUserData(fileName, null) { success ->
                    if (success) {
                        showToast("User Updated Successfully!", requireContext())
                        navController.popBackStack()
                    } else {
                        showToast("Failed to Update User!", requireContext())
                    }
                }
            } else {
                showToast("Something happened during the image upload!", requireContext())
            }
        }
    }

    private suspend fun addImageToStorage(bitmap: Bitmap): Boolean {
        return suspendCoroutine { continuation ->
            val storage = Firebase.storage
            val storageRef = storage.reference

            // Convert Bitmap to byte array
            val stream = ByteArrayOutputStream()
            var quality = 100
            var byteArray: ByteArray

            do {
                stream.reset() // Reset stream before each compression attempt
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
                byteArray = stream.toByteArray()
                quality -= 10 // Adjust compression quality

            } while (byteArray.size > 1 * 1024 * 1024 && quality > 0) // Check if the size exceeds 1MB

            // Create a unique filename (you may adjust this logic according to your requirements)
            fileName = if (pathToImage != "userImages/default_user.png") {
                pathToImage
            } else {
                "userImages/${System.currentTimeMillis()}.jpg"
            }

            // Upload the byte array to Firebase Storage
            val photoRef = storageRef.child("$fileName")
            val uploadTask = photoRef.putBytes(byteArray)

            // Monitor the upload task
            uploadTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(true)
                } else {
                    continuation.resume(false)
                }
            }
        }
    }

    private suspend fun updateUserData(
        imagePath: String?,
        username: String?,
        callback: (Boolean) -> Unit
    ) {
        val userUpdates = mutableMapOf<String, String>()

        if (username != null && username != usernameGlobal) {
            userUpdates["username"] = username
        }

        if (imagePath != null) {
            userUpdates["pathToImage"] = imagePath
        }

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

    private fun validateAndUpdateUsername() {
        val username = binding.editTextUsername.text.toString().trim()

        formIsValid = true

        if (username.isEmpty() || !isValidUsername(username)) {
            setErrorAndFocus(binding.textInputLayoutUsername, "Invalid Username!")
            formIsValid = false
        }

        if (formIsValid == true) {
            val appDatabase = AppDatabase.getInstance(requireContext())
            if (appDatabase != null) {
                lifecycleScope.launch {
                    if (bitMap != null) {
                        val imageUploaded = addImageToStorage(bitMap!!)
                        if (imageUploaded) {
                            updateUserData(fileName, username) { success ->
                                if (success) {
                                    showToast("User Updated Successfully!", requireContext())
                                    navController.popBackStack()
                                } else {
                                    showToast("Failed to Update User!", requireContext())
                                }
                            }
                        } else {
                            showToast(
                                "Something happened during the image upload!",
                                requireContext()
                            )
                        }
                    } else {
                        updateUserData(null, username) { success ->
                            if (success) {
                                showToast("User Updated Successfully!", requireContext())
                                navController.popBackStack()
                            } else {
                                showToast("Failed to Update User!", requireContext())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun validateAndUpdatePassword() {
        val username = binding.editTextUsername.text.toString().trim()
        val oldPassword = binding.editTextOldPassword.text.toString().trim()
        val newPassword = binding.editTextPassword.text.toString().trim()
        val repeatPassword = binding.editTextRepeatPassword.text.toString().trim()

        formIsValid = true

        // Validate old password
        if (oldPassword.isEmpty()) {
            setErrorAndFocus(binding.textInputLayoutOldPassword, "Required!")
            formIsValid = false
        } else if (!isValidPassword(oldPassword)) {
            setErrorAndFocus(binding.textInputLayoutOldPassword, "Invalid Password!")
            formIsValid = false
        }

        // Validate new password
        if (newPassword.isEmpty()) {
            setErrorAndFocus(binding.textInputLayoutPassword, "Required!")
            formIsValid = false
        } else if (!isValidPassword(newPassword)) {
            setErrorAndFocus(binding.textInputLayoutPassword, "Invalid Password!")
            formIsValid = false
        }

        // Validate repeat password
        if (repeatPassword.isEmpty()) {
            setErrorAndFocus(binding.textInputLayoutRepeatPassword, "Required!")
            formIsValid = false
        } else if (!isValidPassword(repeatPassword)) {
            setErrorAndFocus(binding.textInputLayoutRepeatPassword, "Invalid Password!")
            formIsValid = false
        }

        // Validate old password with new password and if the new password is the same as repeat password
        if (oldPassword == newPassword) {
            setErrorAndFocus(binding.textInputLayoutOldPassword, "Must be Different!")
            setErrorAndFocus(binding.textInputLayoutPassword, "Must be Different!")
            formIsValid = false
        }
        if (newPassword != repeatPassword) {
            setErrorAndFocus(binding.textInputLayoutPassword, "Must be the same!")
            setErrorAndFocus(binding.textInputLayoutRepeatPassword, "Must be the same!")
            formIsValid = false
        }


        if (formIsValid == true) {
            user?.let { currentUser ->
                val credential =
                    EmailAuthProvider.getCredential(currentUser.email!!, oldPassword)

                currentUser.reauthenticate(credential)
                    .addOnCompleteListener { reAuthTask ->
                        if (reAuthTask.isSuccessful) {
                            // User has been successfully re-authenticated
                            // Now you can update the password
                            lifecycleScope.launch {
                                if (username != usernameGlobal || bitMap != null) {
                                    if (username != usernameGlobal && bitMap == null) {
                                        updateUserData(null, username) { success ->
                                            if (success) {
                                                currentUser.updatePassword(newPassword)
                                                    .addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            showToast(
                                                                "User data successfully updated!",
                                                                requireContext()
                                                            )
                                                            navController.popBackStack()
                                                        } else {
                                                            showToast(
                                                                "Error updating password!",
                                                                requireContext()
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
                                    } else if (username == usernameGlobal && bitMap != null) {
                                        val imageUploaded = addImageToStorage(bitMap!!)
                                        if (imageUploaded) {
                                            updateUserData(fileName, null) { success ->
                                                if (success) {
                                                    currentUser.updatePassword(newPassword)
                                                        .addOnCompleteListener { task ->
                                                            if (task.isSuccessful) {
                                                                showToast(
                                                                    "User data successfully updated!",
                                                                    requireContext()
                                                                )
                                                                navController.popBackStack()
                                                            } else {
                                                                showToast(
                                                                    "Error updating password!",
                                                                    requireContext()
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
                                            showToast(
                                                "Something happened during the image upload!",
                                                requireContext()
                                            )
                                        }
                                    } else {
                                        val imageUploaded = addImageToStorage(bitMap!!)
                                        if (imageUploaded) {
                                            updateUserData(fileName, username) { success ->
                                                if (success) {
                                                    currentUser.updatePassword(newPassword)
                                                        .addOnCompleteListener { task ->
                                                            if (task.isSuccessful) {
                                                                showToast(
                                                                    "User data successfully updated!",
                                                                    requireContext()
                                                                )
                                                                navController.popBackStack()
                                                            } else {
                                                                showToast(
                                                                    "Error updating password!",
                                                                    requireContext()
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
                                            showToast(
                                                "Something happened during the image upload!",
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
                                                navController.popBackStack()
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
                            showToast("Re-authentication failed", requireContext())
                            setErrorAndFocus(
                                binding.textInputLayoutOldPassword,
                                "Incorrect password!"
                            )
                        }
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

// take photos

    val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    bitMap = rotateBitmap(currentPhotoPath)
                    binding.imageViewUser.setImageBitmap(bitMap)
                }
            }

            REQUEST_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (data.data != null) {
                        val inputStream =
                            requireContext().contentResolver.openInputStream(data.data!!)
                        bitMap = inputStream.use {
                            BitmapFactory.decodeStream(it)
                        }
                        binding.imageViewUser.setImageBitmap(bitMap)
                    }
                }
            }
        }
    }

    private fun askForCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.CAMERA),
            requestCodeCameraPermission
        )
    }

    private fun rotateBitmap(photoPath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        val originalBitmap =
            BitmapFactory.decodeFile(photoPath, options) ?: return Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            )

        try {
            val exif = ExifInterface(photoPath)
            val orientation =
                exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            return Bitmap.createBitmap(
                originalBitmap,
                0,
                0,
                originalBitmap.width,
                originalBitmap.height,
                matrix,
                true
            )
        } catch (e: IOException) {
            e.printStackTrace()
            return originalBitmap
        } catch (e: NullPointerException) {
            e.printStackTrace()
            return originalBitmap
        }
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    fun createImageFile(): File {
        val timeStamp: String = UUID.randomUUID().toString()
        val storageDir: File =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        val items = arrayOf<CharSequence>("Tirar Foto", "Escolher da Galeria")
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Escolher uma Opção")
        builder.setItems(items) { _, item ->
            when {
                items[item] == "Tirar Foto" -> {
                    if (ContextCompat.checkSelfPermission(
                            requireContext(), android.Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        takePhoto()
                    } else {
                        askForCameraPermission()
                    }
                }
                items[item] == "Escolher da Galeria" -> {
                    pickFromGallery()
                }
            }
        }
        builder.show()
    }

    private fun takePhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.museumexplore.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun pickFromGallery() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE)
    }
}