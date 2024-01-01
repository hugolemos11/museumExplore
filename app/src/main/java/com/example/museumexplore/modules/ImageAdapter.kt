package com.example.museumexplore.modules

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.museumexplore.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ImageAdapter(private val list: List<Image>, private val context: Context) :
    RecyclerView.Adapter<ImageAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val carouselImageView: ImageView = view.findViewById(R.id.carouselImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapter.ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.images_carousel, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageAdapter.ItemViewHolder, position: Int) {
        val model = list[position]

        model.pathToImage?.let { imagePath ->
            val storage = Firebase.storage
            val storageRef = storage.reference
            val pathReference = storageRef.child(imagePath)
            pathReference.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(context)
                    .load(uri)
                    .into(holder.carouselImageView)
            }.addOnFailureListener {
                // Handle any errors
            }
        }
    }

    override fun getItemCount() = list.size


}