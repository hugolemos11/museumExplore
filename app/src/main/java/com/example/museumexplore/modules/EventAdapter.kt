package com.example.museumexplore.modules

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.museumexplore.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class EventAdapter(private val list: List<Event>, val context: Context, val onItemClick: (Event) -> Unit) :
    RecyclerView.Adapter<EventAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(val view: View) :
        RecyclerView.ViewHolder(view) {
        val imageViewEvent: ImageView = view.findViewById(R.id.imageViewEvent)
        val textViewEventTitle: TextView = view.findViewById(R.id.textViewEventTitle)
        val textViewEventDescription: TextView = view.findViewById(R.id.textViewEventDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdapter.ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.events_carousel, parent, false)
        )
    }

    override fun onBindViewHolder(holder: EventAdapter.ItemViewHolder, position: Int) {
        val model = list[position]

        model.pathToImage?.let { imagePath ->
            val storage = Firebase.storage
            val storageRef = storage.reference
            val pathReference = storageRef.child(imagePath)
            pathReference.downloadUrl.addOnSuccessListener { uri ->
                Log.e("URI", "${uri}")
                Glide.with(context)
                    .load(uri)
                    .into(holder.imageViewEvent)
                holder.textViewEventTitle.text = model.title
                holder.textViewEventDescription.text = model.description
                holder.itemView.setOnClickListener {
                    onItemClick(model)
                }
            }.addOnFailureListener {
                // Handle any errors
            }
        }
    }

    override fun getItemCount() = list.size
}