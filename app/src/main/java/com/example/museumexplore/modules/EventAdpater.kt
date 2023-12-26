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

class EventAdpater(val list: ArrayList<Event>, val context: Context, val onItemClick: (Event) -> Unit) : RecyclerView.Adapter<EventAdpater.ItemViewHolder>() {
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