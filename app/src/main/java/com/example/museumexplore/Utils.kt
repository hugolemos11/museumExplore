package com.example.museumexplore

import android.content.Context
import android.renderscript.ScriptGroup.Binding
import android.widget.Adapter
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.museumexplore.modules.Image
import com.example.museumexplore.modules.ImageAdapter
import com.example.museumexplore.modules.Museum
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

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