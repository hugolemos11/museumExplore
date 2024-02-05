package com.example.museumexplore.ui.other

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
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
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import java.util.UUID
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.log

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    private var userId: String? = null
    private var username: String? = null
    private var pathToImage: String? = null
    private val user = Firebase.auth.currentUser
    private val db = Firebase.firestore
    private var usernamesInUse: ArrayList<String> = ArrayList()
    private var formIsValid: Boolean? = null

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

        navController = Navigation.findNavController(view)

        setImage(pathToImage, binding.imageViewUser, requireContext())


        binding.imageViewEditPhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }
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

            updateImage()

            if ((username.isNotEmpty() && oldPassword.isEmpty()) || (username.isNotEmpty() && oldPassword.isNotEmpty() && newPassword.isEmpty())) {
                validateAndUpdateUsername()
            } else if (username.isNotEmpty() && oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                validateAndUpdatePassword()
            }
        }

        fetchUsernamesData()
    }

    private fun updateImage() {
        lifecycleScope.launch {
            val userImagePath = uploadImageToFirebaseStorage()

            Log.e("TESTE", userImagePath)

                if (username != this@EditProfileFragment.username) {
                    username?.let {
                        updateUsernameAndImage(it, userImagePath) { success ->
                            if (success) {
                                showToast("User Updated Successfully!", requireContext())
                                navController.popBackStack()
                            } else {
                                showToast("Failed to Update User!", requireContext())
                            }
                        }
                    }
                } else {
                    showToast("Change some data!", requireContext())
                }
        }
    }

    private suspend fun uploadImageToFirebaseStorage(): String {
        return try {
            val storageReference = Firebase.storage.reference
            val imageRef = storageReference.child("userImages/${UUID.randomUUID()}.jpg")

            val userImageBitmap = (binding.imageViewUser.drawable as BitmapDrawable).bitmap
            val outputStream = ByteArrayOutputStream()
            userImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val data = outputStream.toByteArray()

            val uploadTask = imageRef.putBytes(data).asDeferred()

            try {
                viewLifecycleOwner.lifecycleScope.coroutineContext.ensureActive()
                uploadTask.await() // Wait for the upload task to complete

                // Check if the coroutine job is still active before proceeding
                if (!isActive) {
                    "erro"
                } else {
                    // If no exception occurred during upload, consider it successful
                    "success"
                }
            } catch (e: CancellationException) {
                Log.e("TESTE", "Error uploading image to Firebase Storage: ${e.message}", e)
                "erro"
            }
        } catch (e: Exception) {
            Log.e("TESTE", "Error uploading image to Firebase Storage: ${e.message}", e)
            "erro"
        }
    }

    // Extension function to convert Task to Deferred
    private fun <T> Task<T>.asDeferred(): Deferred<T> {
        val deferred = CompletableDeferred<T>()

        this.addOnSuccessListener { result ->
            deferred.complete(result)
        }

        this.addOnFailureListener { exception ->
            deferred.completeExceptionally(exception)
        }

        return deferred
    }



    private suspend fun updateUsernameAndImage(username: String, imagePath: String, callback: (Boolean) -> Unit) {
        val userUpdates = mapOf("username" to username, "pathToImage" to imagePath)
        val appDatabase = AppDatabase.getInstance(requireContext())

        Log.e("TESTE", userUpdates.toString())

        if (appDatabase != null) {
            userId?.let { currentUserId ->
                try {
                    val updatedUserData = User.updateUserData(currentUserId, userUpdates).await()
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
                            navController.popBackStack()
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
                            // User has been successfully re-authenticated
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

    // take photos

    val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val rotatedBitmap = rotateBitmap(currentPhotoPath)
                    binding.imageViewUser.setImageBitmap(rotatedBitmap)
                }
            }
            REQUEST_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (data.data != null) {
                        binding.imageViewUser.setImageURI(data.data)
                    }
                }
            }
        }
    }

    private fun rotateBitmap(photoPath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888

        val originalBitmap = BitmapFactory.decodeFile(photoPath, options) ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        try {
            val exif = ExifInterface(photoPath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            }

            return Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
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
    private fun createImageFile(): File {
        val timeStamp: String = UUID.randomUUID().toString()
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
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
                    takePhoto()
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
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_PICK_IMAGE)
    }
}