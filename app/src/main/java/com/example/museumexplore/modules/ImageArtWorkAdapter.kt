package com.example.museumexplore.modules

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.museumexplore.R
import com.example.museumexplore.setImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ImageArtWorkAdapter: ListAdapter<ImageArtWork,ImageArtWorkAdapter.ViewHolder>(DiffCallback()){
    class DiffCallback : DiffUtil.ItemCallback<ImageArtWork>(){
        override fun areItemsTheSame(oldItem: ImageArtWork, newItem: ImageArtWork): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImageArtWork, newItem: ImageArtWork): Boolean {
            return oldItem == newItem
        }

    }
    class ViewHolder(iteView: View): RecyclerView.ViewHolder(iteView){
        private val imageView = iteView.findViewById<ImageView>(R.id.imageView)

        fun bindData(item: ImageArtWork){
            item.pathToImage?.let {
                val storage = Firebase.storage
                val storageRef = storage.reference
                val pathReference = storageRef.child(it)
                pathReference.downloadUrl.addOnSuccessListener { uri ->
                    Glide.with(itemView)
                        .load(uri)
                        .into(imageView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.image_display_slider,parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageItem = getItem(position)
        holder.bindData(imageItem)
    }
}