package com.example.museumexplore

import android.content.Context
import android.renderscript.ScriptGroup.Binding
import android.widget.Adapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.museumexplore.modules.Image
import com.example.museumexplore.modules.ImageAdapter
import com.example.museumexplore.modules.Museum
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

val emailRegex = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}\$"
val usernameRegex = "^[a-zA-Z0-9_.-]{6,20}\$"
val passwordRegex =
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+{}\\[\\]:;<>,.?~\\\\-]).{8,}\$"


fun showToast(message: String, context: Context) {
    Toast.makeText(context, message, Toast.LENGTH_LONG)
        .show()
}

fun setImage(pathToImage: String?, imageView: ImageView, context: Context) {
    pathToImage?.let { imagePath ->
        // Load the image from Firebase Storage
        val storage = Firebase.storage
        val storageRef = storage.reference
        val pathReference = storageRef.child(imagePath)
        pathReference.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(context)
                .load(uri)
                .into(imageView)
        }.addOnFailureListener {
            showToast("An error occurred: ${it.localizedMessage}", context)
        }
    }
}

// Regex

fun isValidEmail(email: String): Boolean {
    return email.matches(emailRegex.toRegex())
}

fun isValidUsername(username: String): Boolean {
    return username.matches(usernameRegex.toRegex())
}

fun isValidPassword(password: String): Boolean {
    return password.matches(passwordRegex.toRegex())
}

fun setErrorAndFocus(editText: TextInputLayout, error: String) {
    editText.requestFocus()
    editText.error = error

}