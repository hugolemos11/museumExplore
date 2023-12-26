package com.example.museumexplore.modules

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.museumexplore.databinding.EventsCarouselBinding
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EventAdapter(private val list: ArrayList<Event>, val context: Context, val onItemClick: (Event) -> Unit) : RecyclerView.Adapter<EventAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(val binding: EventsCarouselBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: Event, bitmap: Bitmap) {
            binding.apply {
                imageViewEvent.setImageBitmap(bitmap)
                textViewTitle.text = model.title
                textViewDescription.text = model.description
            }

            itemView.setOnClickListener {
                onItemClick(model)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.ItemViewHolder {
        return ItemViewHolder(
            EventsCarouselBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EventAdapter.ItemViewHolder, position: Int) {
        val model = list[position]
        model.pathToImage?.let { imagePath ->
            // Load the image from Firebase Storage
            val storage = Firebase.storage
            val storageRef = storage.reference
            val pathReference = storageRef.child(imagePath)
            val ONE_MEGABYTE: Long = 10 * 1024 * 1024
            pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener { data ->
                val bitmap = BitmapFactory.decodeByteArray(data, 0, data.count())
                holder.bind(model, bitmap)
            }.addOnFailureListener {
                // Handle any errors
                Log.e("MuseumDetailsFragment", "Failed to load image from Firebase Storage")
            }
        }

    }


    override fun getItemCount() = list.size
}