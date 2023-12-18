package com.example.museumexplore.modules

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.museumexplore.databinding.EventsCarouselBinding

class EventAdpater(val list: ArrayList<EventsModel>, val context: Context) : RecyclerView.Adapter<EventAdpater.ItemViewHolder>() {
    inner class ItemViewHolder(val binding: EventsCarouselBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: EventsModel) {
            binding.apply {
                imageViewEvent.setImageResource(model.imageId)
                textViewTitle.text = model.title
                textViewDescription.text = model.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdpater.ItemViewHolder {
        return ItemViewHolder(
            EventsCarouselBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: EventAdpater.ItemViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size
}





/*class EventsPagerAdapter(val events: List<EventsModel>, val context: Context) :
    PagerAdapter() {

    private lateinit var binding: EventsPageBinding

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        binding = EventsPageBinding.inflate(LayoutInflater.from(context))
        val imageViewEvent = binding.imageViewEvent
        val textViewEventTitle = binding.textViewEventTitle
        val textViewEventDescription = binding.textViewEventDescription

        // Assuming Event class has getImageResId(), getTitle(), and getDescription() methods
        val currentEvent: EventsModel = events[position]
        imageViewEvent.setImageResource(currentEvent.imageId)
        textViewEventTitle.text = currentEvent.title
        textViewEventDescription.text = currentEvent.description
        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return events.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

}*/