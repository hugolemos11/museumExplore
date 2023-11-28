package com.example.museumexplore.modules

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.museumexplore.databinding.ImagesCarouselBinding

class ImageAdapter(val list: ArrayList<ImagesModel>, val context: Context) : RecyclerView.Adapter<ImageAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(val binding: ImagesCarouselBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ImagesModel) {
            binding.apply {
                carouselImageView.setImageResource(model.imageId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAdapter.ItemViewHolder {
        return ItemViewHolder(ImagesCarouselBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ImageAdapter.ItemViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size


}