package com.example.museumexplore.modules

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.museumexplore.databinding.ImagesCarouselBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ImageAdapter(private val list: ArrayList<Image>, val context: Context) : RecyclerView.Adapter<ImageAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(val binding: ImagesCarouselBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Bitmap) {
            binding.apply {
                carouselImageView.setImageBitmap(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapter.ItemViewHolder {
        return ItemViewHolder(ImagesCarouselBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageAdapter.ItemViewHolder, position: Int) {
        val model = list[position]
        model.pathToImage?.let { imagePath ->
            // Load the image from Firebase Storage
            val storage = Firebase.storage
            val storageRef = storage.reference
            val pathReference = storageRef.child(imagePath)
            val ONE_MEGABYTE: Long = 10 * 1024 * 1024
            pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { data ->
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.count())
                holder.bind(bitmap)
            }.addOnFailureListener {
                // Handle any errors
                Log.e("MuseumDetailsFragment", "Failed to load image from Firebase Storage")
            }
        }

    }

    override fun getItemCount() = list.size


}