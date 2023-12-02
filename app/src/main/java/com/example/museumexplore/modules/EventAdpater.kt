package com.example.museumexplore.modules

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.museumexplore.databinding.EventsCarouselBinding
import com.example.museumexplore.databinding.ImagesCarouselBinding

class EventAdpater(val list: ArrayList<EventsModel>, val context: Context) : RecyclerView.Adapter<EventAdpater.ItemViewHolder>() {
    inner class ItemViewHolder(val binding: EventsCarouselBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: EventsModel) {
            binding.apply {
                imageViewEvent.setImageResource(model.imageId)
                textViewTitle.text = model.title
                textViewDescription.text = model.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdpater.ItemViewHolder {
        return ItemViewHolder(EventsCarouselBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: EventAdpater.ItemViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size


}